package client;

import interfaces.INetClient;
import interfaces.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.ActivityType;
import commonResources.model.UserStat;

/**
 * @author Shmoylova Kseniya
 *  
 * Class responsible for the network part of the application
 * Init socket connection
 * Create proxy instance  {@link NetClient#clientElement} with parameters: <br>
 * The ClassLoader that is to "load" the dynamic proxy class.
 * An interface to implement.
 * An InvocationHandler to forward all methods calls to the proxy.
 */
public class NetClient implements INetClient {
	
	private static final Logger log = Logger.getLogger(NetClient.class);
    private static final NetClient INSTANCE = new NetClient();
    private static final String CFG_PATH = "./target/classes/config/netclient.properties";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String FORMAT = "1";
    private static String host;
    private static int port;
    private Socket socket;
	private IVariableEssence clientElement;	
    private List<Observer> observers;
    private ActivityType activityTypes;
    private UserStat statistic;

    /**
     * Constructor
     * Read properties file with connection defaults
     * Lookup serverSocket and get socket instance
     * Instantiate InvocationHandler and Proxy objects
     * @resource netclient.properties
     */
	private NetClient() {
		this.observers=new ArrayList<>();
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(CFG_PATH)));
			port = Integer.valueOf(props.getProperty(PORT, FORMAT));
			host = props.getProperty(HOST);
			socket = new Socket(host, port);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		ClientProxy proxy = new ClientProxy();
		proxy.socket = socket;

		clientElement = (IVariableEssence)Proxy.newProxyInstance(ClientProxy.class.getClassLoader(),
				new Class<?>[] {IVariableEssence.class}, proxy);
	}
	
	/**
	 * Get singleton 
	 * @return {@link NetClient#INSTANCE} 
	 */
	public static NetClient getInstance(){
		return INSTANCE;
	}

	/**
	 * Invoke server method for getting tree of the activity types 
	 * and notifies all subscribers about changes
	 */
	@Override
	public void loadActivityTypes() {
		activityTypes = clientElement.getActivityTypesTree();
		notifyObservers(this.activityTypes);
	}

	/**
	 * Invoke server method for setting tree of the activity types 
	 */
	@Override
	public void setActivityTypesTree(ActivityType activityType) {
		clientElement.setActivityTypesTree(activityType);	
	}

	/**
	 * Invoke server method for getting user statistic 
	 * and notifies all subscribers about changes
	 * @param userName - registered name of user
	 * @param firstDate - start date of the requested time interval
	 * @param lastDate - last date of the requested time interval
	 */
	@Override
	public void loadStat(String userName, LocalDate firstDate, LocalDate lastDate) {
		statistic =	clientElement.getUserStat(userName, firstDate, lastDate);
		notifyObservers(this.statistic);
	}

	/**
	 * Invoke server method for matches if the user name from the list
	 * If true - {@link NetClient#loadActivityTypes()}
	 * @param userName - tested name
	 * @return true - if matches, false - not
	 */
	@Override
	public boolean login(String userName) {
		boolean checkAccepted =clientElement.login(userName);
		if(checkAccepted){
			loadActivityTypes();
		}
		return checkAccepted;
	}
	
	/**
	 * Method register subscriber in {@link NetClient#observers}
	 */
	@Override
	public void register(Observer obj) {
		if(obj == null) 
			throw new NullPointerException();
		if(!observers.contains(obj)) {
			observers.add(obj);
		}
	}
	
	/**
	 * Method unregister subscriber from {@link NetClient#observers}
	 */
	@Override
	public void unregister(Observer obj) {
		observers.remove(obj);
	}

	/**
	 * Method notifies all subscribers from {@link NetClient#observers} about changes
	 * @param object - changes
	 */
	@Override
	public void notifyObservers(Object object) {
		for (Observer obj : observers) 
			obj.update(object);
	}

	/**
	 * Invoke server method to perform actions associated with the event - user disconnected from the server
	 * @param userName - disconnecting user
	 * @param statistic - current statistic of this user
	 */
	@Override
	public void disconnect(String userName, UserStat statistic) {
		clientElement.setUserStat(userName, statistic);
		clientElement.disconnect(userName);
	}

	/**
	 * Invoke server method to set last selected activity type
	 * @param activityID - Id of selected type 
	 */
	@Override
	public void setCurrentActivityElement(int activityID) {
		// TODO Auto-generated method stub
	}
} 

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

	public NetClient() {
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
        // Создаём прокси
		ClientProxy proxy = new ClientProxy();
		proxy.socket = socket;

		// Создаём экземпляр прокси-объекта
		clientElement = (IVariableEssence)Proxy.newProxyInstance(ClientProxy.class.getClassLoader(),
				new Class<?>[] {IVariableEssence.class}, proxy);
	}
	

	public static NetClient getInstance(){
		return INSTANCE;
	}

	@Override
	public void loadActivityTypes() {
		activityTypes = clientElement.getActivityTypesTree();
		notifyObservers(this.activityTypes);
	}

	@Override
	public void setActivityTypesTree(ActivityType activityType) {
		clientElement.setActivityTypesTree(activityType);	
	}

	@Override
	public void loadStat(String userName, LocalDate firstDate, LocalDate lastDate) {
		statistic =	clientElement.getUserStat(userName, firstDate, lastDate);
		notifyObservers(this.statistic);
	}

	@Override
	public boolean login(String userName) {
		boolean checkAccepted =clientElement.login(userName);
		if(checkAccepted){
			loadActivityTypes();
		}
		return checkAccepted;
	}

	@Override
	public void register(Observer obj) {
		if(obj == null) 
			throw new NullPointerException();
		if(!observers.contains(obj)) {
			observers.add(obj);
		}
	}

	@Override
	public void unregister(Observer obj) {
		observers.remove(obj);
	}

	@Override
	public void notifyObservers(Object object) {
		for (Observer obj : observers) 
			obj.update(object);
	}

	@Override
	public void disconnect(String userName, UserStat statistic) {
		clientElement.setUserStat(userName, statistic);
		clientElement.disconnect(userName);
	}

	@Override
	public void setCurrentActivityElement(int activityID) {
		// TODO Auto-generated method stub
	}
} 

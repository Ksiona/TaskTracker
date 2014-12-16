package client;

import interfaces.INetClient;
import interfaces.Observer;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.ActivityType;
import commonResources.model.TrackerUser;
import commonResources.model.UserStat;

public class NetClient implements INetClient {
	private Socket socket;
	private IVariableEssence clientElement;	
    private List<Observer> observers;
    ActivityType activityTypes;
    private static final NetClient INSTANCE = new NetClient();

	public NetClient() {
		this.observers=new ArrayList<>();
        // Создаём прокси
		try {
			socket = new Socket("localhost", 6284);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClientProxy p = new ClientProxy();
		p.socket = socket;

		// Создаём экземпляр прокси-объекта
		clientElement = (IVariableEssence)Proxy.newProxyInstance(ClientProxy.class.getClassLoader(),
				new Class<?>[] {IVariableEssence.class},
				p);
	}
	
	public static NetClient getInstance(){
		return INSTANCE;
	}
    
	public static void print(ActivityType activityType){
		System.out.println(activityType);
		for (ActivityType t: activityType.getActivityType())
			print(t);
	}

	@Override
	public void loadActivityTypes() {
		activityTypes = clientElement.getActivityTypesTree();
		notifyObservers();
	}

	@Override
	public void setActivityTypesTree(ActivityType activityType) {
		clientElement.setActivityTypesTree(activityType);	
	}

	@Override
	public void loadStat(TrackerUser user, Date firstDate, Date lastDate) {
		// TODO Auto-generated method stub
		clientElement.getUserStat(user.getUserName(), firstDate, lastDate);
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
	public void notifyObservers() {
		for (Observer obj : observers) 
			obj.update(this.activityTypes);
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

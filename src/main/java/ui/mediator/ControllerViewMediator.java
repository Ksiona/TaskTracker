package ui.mediator;

import java.io.File;
import java.util.Date;

import client.NetClient;
import commonResources.model.Task;
import commonResources.model.TrackerUser;
import commonResources.model.UserStat;
import interfaces.IControllerViewMediator;
import interfaces.INetClient;
import interfaces.Observer;
import logic.TaskProcessor;


public class ControllerViewMediator implements IControllerViewMediator  {

	private static TaskProcessor tp;
	private INetClient nc;
	private static final ControllerViewMediator INSTANCE = new ControllerViewMediator();
	
	private ControllerViewMediator() {
		tp = TaskProcessor.getInstance();
		nc = NetClient.getInstance();
	}
	
	public static ControllerViewMediator getInstance(){
		return INSTANCE;
	}
	
	@Override
	public void loadTasks() {
		nc.loadTasks();
	}

	@Override
	public void register(Observer obj) {
		nc.register(obj);
	}
	
	@Override
	public void unregister(Observer obj) {
		nc.unregister(obj);
		
	}

	@Override
	public void setTaskTree(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(TrackerUser user, Date firstDate, Date lastDate) {
		nc.loadStat(user, firstDate, lastDate);
	}

	@Override
	public void writeFileStat(File selectedDir, UserStat userStat) {
		tp.writeFileStat(selectedDir, userStat);
	}
	
	@Override
	public void readFileStat(File selectedFile) {
		tp.readFileStat(selectedFile);
	}

	@Override
	public void setCurrentTaskElement(int taskID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertTaskElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editTaskElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTaskElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObservers() {
		// Nothing to do		
	}

	@Override
	public boolean login(String userName) {
		return nc.login(userName);
	}

	@Override
	public void disconnect(String userName, UserStat statistic) {
		nc.disconnect(userName, statistic);
	}
}

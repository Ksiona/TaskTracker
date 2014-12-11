package ui.mediator;

import java.util.Date;

import client.NetClient;
import commonResources.model.Task;
import commonResources.model.TrackerUser;
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
	public void loadStat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(Date date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(TrackerUser user, Date firstDate, Date lastDay) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFileStat() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void readFileStat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrentTaskElement() {
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
	public void login() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
}

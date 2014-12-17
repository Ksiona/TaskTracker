package ui.mediator;

import interfaces.IControllerViewMediator;
import interfaces.INetClient;
import interfaces.Observer;

import java.io.File;
import java.time.LocalDate;

import logic.ActivityTypeProcessor;
import client.NetClient;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;


public class ControllerViewMediator implements IControllerViewMediator  {

	private static ActivityTypeProcessor tp;
	private INetClient nc;
	private static final ControllerViewMediator INSTANCE = new ControllerViewMediator();
	
	private ControllerViewMediator() {
		tp = ActivityTypeProcessor.getInstance();
		nc = NetClient.getInstance();
	}
	
	public static ControllerViewMediator getInstance(){
		return INSTANCE;
	}
	
	@Override
	public void loadActivityTypes() {
		nc.loadActivityTypes();
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
	public void setActivityTypesTree(ActivityType activityType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(String userName, LocalDate firstDate, LocalDate lastDate) {
		nc.loadStat(userName, firstDate, lastDate);
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
	public void setCurrentActivityElement(int activityTypeID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObservers(Object object) {
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

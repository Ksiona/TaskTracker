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

	private static final ControllerViewMediator INSTANCE = new ControllerViewMediator();
	private static ActivityTypeProcessor tp;
	private INetClient nc;

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
	public void setActivityTypesTree() {
		nc.setActivityTypesTree(tp.getActivityTypes());
	}
	
	@Override
	public void setActivityTypesTree(ActivityType activityType) {
		// Nothing to do	
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
	public ActivityType insertActivityTypeElement(int activityTypeID) {
		return tp.insertActivityTypeElement(activityTypeID);
	}

	@Override
	public void editActivityTypeElement() {
		tp.editActivityTypeElement();
	}

	@Override
	public void removeActivityTypeElement(ActivityType activityType) {
		tp.removeActivityTypeElement(activityType);
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

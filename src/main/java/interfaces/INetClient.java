package interfaces;

import java.time.LocalDate;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public interface INetClient {
	public void register(Observer obj);
	public void unregister(Observer obj);
	default void notifyObservers(Object object){};
	
	public void loadActivityTypes();
	default void setActivityTypesTree(ActivityType activityType){};
	public void setCurrentActivityElement(int activityTypeID);
	public void loadStat(String userName, LocalDate firstDate,LocalDate lastDate);
	public boolean login(String userName);
	public void disconnect(String userName, UserStat statistic);

	
}

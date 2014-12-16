package interfaces;

import java.util.Date;

import commonResources.model.ActivityType;
import commonResources.model.TrackerUser;
import commonResources.model.UserStat;

public interface INetClient {
	public void register(Observer obj);
	public void unregister(Observer obj);
	public void notifyObservers();
	
	public void loadActivityTypes();
	public void setActivityTypesTree(ActivityType activityType);
	public void setCurrentActivityElement(int activityTypeID);
	public void loadStat(TrackerUser user, Date firstDate, Date lastDate);
	public boolean login(String userName);
	public void disconnect(String userName, UserStat statistic);
}

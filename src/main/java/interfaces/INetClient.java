package interfaces;

import java.util.Date;

import commonResources.model.Task;
import commonResources.model.TrackerUser;
import commonResources.model.UserStat;

public interface INetClient {
	public void register(Observer obj);
	public void unregister(Observer obj);
	public void notifyObservers();
	
	public void loadTasks();
	public void setTaskTree(Task task);
	public void setCurrentTaskElement(int taskID);
	public void loadStat(TrackerUser user, Date firstDate, Date lastDay);
	public boolean login(String userName);
	public void disconnect(String userName, UserStat statistic);
}

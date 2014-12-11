package interfaces;

import java.util.Date;

import commonResources.model.Task;
import commonResources.model.TrackerUser;

public interface INetClient {
	public void register(Observer obj);
	public void unregister(Observer obj);
	public void notifyObservers();
	
	public void loadTasks();
	public void setTaskTree(Task task);
	public void loadStat();
	public void loadStat(Date date);
	public void loadStat(TrackerUser user, Date firstDate, Date lastDay);
	public void login();
	public void disconnect();
}

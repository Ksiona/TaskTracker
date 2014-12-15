package commonResources.interfaces;

import java.util.Date;

import commonResources.model.Task;
import commonResources.model.UserStat;

public interface IVariableEssence {
	public boolean login(String userName);
    public void disconnect(String userName);
    public Task getTaskTree();
    public void setTaskTree(Task task);
    public void setUserStat(String userName, UserStat statistic);
    public UserStat getUserStat(String userName, Date start, Date end);
}

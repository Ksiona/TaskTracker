package commonResources.interfaces;

import java.util.Date;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public interface IVariableEssence {
	public boolean login(String userName);
    public void disconnect(String userName);
    public ActivityType getActivityTypesTree();
    public void setActivityTypesTree(ActivityType activityType);
    public void setUserStat(String userName, UserStat statistic);
    public UserStat getUserStat(String userName, Date start, Date end);
}

package commonResources.interfaces;

import java.time.LocalDate;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public interface IVariableEssence {
	public boolean login(String userName);
    public void disconnect(String userName);
    public ActivityType getActivityTypesTree();
    public void setActivityTypesTree(ActivityType activityType);
    public void setUserStat(String userName, UserStat statistic);
    public UserStat getUserStat(String userName, LocalDate start, LocalDate end);
	
}

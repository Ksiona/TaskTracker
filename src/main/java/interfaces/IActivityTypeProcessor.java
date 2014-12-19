package interfaces;

import java.io.File;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public interface IActivityTypeProcessor {
	
	public void writeFileStat(File selectedFile, UserStat userStat);
	public void readFileStat(File selectedFile);
	public ActivityType insertActivityTypeElement(int activityTypeID);
	public void editActivityTypeElement();
	public void removeActivityTypeElement(ActivityType activityType);
	public void setActivityTypesTree();
}
package interfaces;

import java.io.File;

import commonResources.model.UserStat;


public interface IActivityTypeProcessor {
	
	public void writeFileStat(File selectedFile, UserStat userStat);
	public void readFileStat(File selectedFile);
	public void insertActivityTypeElement();
	public void editActivityTypeElement();
	public void removeActivityTypeElement();
 
}
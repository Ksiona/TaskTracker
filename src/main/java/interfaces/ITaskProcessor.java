package interfaces;

import java.io.File;

import commonResources.model.UserStat;


public interface ITaskProcessor {
	
	public void writeFileStat(File selectedFile, UserStat userStat);
	public void readFileStat(File selectedFile);
	public void insertTaskElement();
	public void editTaskElement();
	public void removeTaskElement();
 
}
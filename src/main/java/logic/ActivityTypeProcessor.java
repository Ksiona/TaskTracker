package logic;

import interfaces.IActivityTypeProcessor;
import interfaces.Observer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public class ActivityTypeProcessor implements IActivityTypeProcessor, Observer {

	private static final ActivityTypeProcessor INSTANCE = new ActivityTypeProcessor();
	private static final String FILE_EXTENSION = ".bin";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static final Logger log = Logger.getLogger(ActivityTypeProcessor.class);
	private ActivityType activityTypes;
	private ActivityType find;
	
	private ActivityTypeProcessor() {
	}
	
	public static ActivityTypeProcessor getInstance(){
		return INSTANCE;
	}
	
/*	private static final String DONT_WORKING = "Don't working";
	private static final String DINNER = "Dinner";
	private static final String REST = "Rest";
	private static final String ANOTHER_WORK = "Work is not associated with PC";
	
 	public static ActivityType getDefaultActivityType() {
		ActivityType activityType = new ActivityType(DONT_WORKING, 1);
		activityType.appendChild(new ActivityType(DINNER, 101000));
		activityType.appendChild(new ActivityType(REST, 101001)); 
		activityType.appendChild(new ActivityType(ANOTHER_WORK, 101002));
		return activityType;
	}*/

	public static void print(ActivityType activityType){
		System.out.println(activityType);
		for (ActivityType t: activityType.getActivityType())
			print(t);
	}
	@Override
	public void writeFileStat(File selectedDir, UserStat userStat) {
		String sDate = FORMAT.format(new Date(System.currentTimeMillis()));
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(
				new FileOutputStream(new File(selectedDir +"/" +sDate+FILE_EXTENSION)))){		
			objectOutStream.writeObject(userStat);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	@Override
	public void readFileStat(File selectedFile) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void insertActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void editActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeActivityTypeElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(ActivityType activityType) {
		this.activityTypes = activityType;
	}
	

	public ActivityType getTreeElement(int activityTypeID) {
		ActivityType activityType = checkID(activityTypes, activityTypeID);
		return activityType;
	}
	
	public ActivityType checkID(ActivityType activityType, int activityTypeID){
		if(activityType.getActivityTypeID()==activityTypeID){
			System.out.println(activityType.getActivityTypeID());
			find = activityType;
		}
		for (ActivityType t: activityType.getActivityType()){
			checkID(t, activityTypeID);
		}
		return find;
	}
}

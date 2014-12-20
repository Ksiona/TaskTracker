package logic;

import interfaces.IActivityTypeProcessor;
import interfaces.INetClient;
import interfaces.Observer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import client.NetClient;
import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public class ActivityTypeProcessor implements IActivityTypeProcessor, Observer {

	private static final Logger log = Logger.getLogger(ActivityTypeProcessor.class);
	private static final ActivityTypeProcessor INSTANCE = new ActivityTypeProcessor();
	private static final String FILE_EXTENSION = ".bin";
	private static final String SLASH = "/";
	private static final String NEW_TYPE_TITLE = "new task";
	private static final String DEFAULT_OWNER = "shared";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private INetClient nc;
	private ActivityType activityTypes;
	private ActivityType find;
	
	private ActivityTypeProcessor() {
		nc = NetClient.getInstance();
		nc.register(this);
	}
	
	public static ActivityTypeProcessor getInstance(){
		return INSTANCE;
	}

	public static void print(ActivityType activityType){
		System.out.println(activityType);
		for (ActivityType t: activityType.getActivityType())
			print(t);
	}
	
	@Override
	public void writeFileStat(File selectedDir, UserStat userStat) {
		String sDate = FORMAT.format(new Date(System.currentTimeMillis()));
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(
				new FileOutputStream(new File(selectedDir +SLASH +sDate+FILE_EXTENSION)))){		
			objectOutStream.writeObject(userStat);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void readFileStat(File selectedFile) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Method get parent of inserting activity type, compute new element ID
	 * and insert new type in parent child list
	 * @see logic.ActivityTypeProcessor#getTreeElement(int)
	 * @see logic.ActivityTypeProcessor#getNumGroops(int)
	 * @see logic.ActivityTypeProcessor#createElement(ActivityType, int)
	 * @param parentTypeID
	 * @return activityType full tree
	 */
	@Override
	public ActivityType insertActivityTypeElement(int parentTypeID) {
		int newID =0;
		ActivityType parent = getTreeElement(parentTypeID);
		int selfNum = parent.getActivityType().size()+1;
		if(parentTypeID != activityTypes.getActivityTypeID()){
			int[] group = getNumGroops(parentTypeID); 
			newID = (group[0]+1)*10000+group[2]*100 +(selfNum);
		}else{
			newID = selfNum*100000+1;
		}
		return createElement(parent, newID);
	}

	/**
	 * Will created new ActivityType with DEFAULT_OWNER = shared
	 * @param parent
	 * @param newID
	 * @return new {@link ActivityType}
	 */
	private ActivityType createElement(ActivityType parent, int newID) {
		ActivityType newElement = new ActivityType(NEW_TYPE_TITLE, newID);
		newElement.setOwner(DEFAULT_OWNER);
		parent.appendChild(newElement);
		return newElement;
	}

	/**
	 * get 3 groups: categoryNum+level - parentNum - selfNum
	 * @param num
	 * @return int array with 3 elements
	 */
	private int[] getNumGroops(int num) {
		int[] array = new int[3];
		int counter = 2;
		while (num != 0) {
			int i = num % 10;
		    num = num / 10; 
		    int j = num % 10;
		    num = num / 10; 
		    array[counter]=j*10+i;
		    counter -=1;
		}
		return array;
	}
	
	@Override
	public void editActivityTypeElement() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void removeActivityTypeElement(ActivityType activityType) {
		removeTreeElement(activityTypes, activityType);
	}
	
	public void removeTreeElement(ActivityType parentType, ActivityType removingType){
		if(parentType.getActivityType().contains(removingType)){
			parentType.getActivityType().remove(removingType);
			return;
		}
		for (ActivityType t: parentType.getActivityType()){
			removeTreeElement(t, removingType);
		}
	}
	
	@Override
	public void setActivityTypesTree() {
		// nothing to do
	}

	@Override
	public void update(Object object) {
		if(object.getClass() == ActivityType.class)
			this.activityTypes = (ActivityType) object;
	}

	public ActivityType getTreeElement(int activityTypeID) {
		ActivityType activityType = checkID(activityTypes, activityTypeID);
		return activityType;
	}
	
	public ActivityType checkID(ActivityType activityType, int activityTypeID){
		if(activityType.getActivityTypeID()==activityTypeID){
			find = activityType;
		}
		for (ActivityType t: activityType.getActivityType()){
			checkID(t, activityTypeID);
		}
		return find;
	}

	public ActivityType getActivityTypes() {
		return activityTypes;
	}
}

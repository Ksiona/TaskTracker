package logic;

import interfaces.IActivityTypeProcessor;
import interfaces.INetClient;
import interfaces.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import client.NetClient;
import commonResources.model.ActivityType;
import commonResources.model.UserStat;

/**
 * @author Shmoylova Kseniya
 * Contains methods for editing a task tree
 * and load/save statistics on the user's device
 */
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
	
	/**
	 * Get singleton 
	 * @return {@link ActivityTypeProcessor#INSTANCE}
	 */
	public static ActivityTypeProcessor getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Method serializing current statistic of user, creates binary file in selected directory
	 * @param selectedDir - directory for save
	 * @param userStat - current statistic
	 * @exception IOException - I/O stream's error, 
	 */
	@Override
	public void writeFileStat(File selectedDir, UserStat userStat) {
		String sDate = FORMAT.format(new Date(System.currentTimeMillis()));
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(
				new FileOutputStream(new File(selectedDir +SLASH +sDate+FILE_EXTENSION)))){		
			objectOutStream.writeObject(userStat);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			// TODO Object return type, to inform the user about the result
		}
	}
	
	/**
	 * Method for deserialize the statistic of user from selected binary file
	 * @param selectedFile -file contains statistic
	 * @exception IOException - I/O stream's error
	 * @exception  ClassCastException | ClassNotFoundException - errors occurs when restoring object
	 */
	@Override
	public UserStat readFileStat(File selectedFile) {
		UserStat statistic = null;
		try (ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(selectedFile));) {
			statistic =((UserStat) objectInStream.readObject());
		} catch (ClassCastException | ClassNotFoundException | IOException e){
			log.warn(e.getMessage(), e);
		}
		return statistic;
	}
	
	/**
	 * Method get parent of inserting activity type, compute new element ID
	 * and insert new type in parent child list
	 * @see logic.ActivityTypeProcessor#getTreeElement(int)
	 * @see logic.ActivityTypeProcessor#getNumGroops(int)
	 * @see logic.ActivityTypeProcessor#createElement(ActivityType, int)
	 * @param parentTypeID
	 * @return new {@link ActivityType}
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
	 * Will created new ActivityType with DEFAULT_OWNER = "shared"
	 * @param parent - ActivityType with list where the new type will inserted
	 * @param newID - computed ID
	 * @return new {@link ActivityType}
	 */
	private ActivityType createElement(ActivityType parent, int newID) {
		ActivityType newElement = new ActivityType(NEW_TYPE_TITLE, newID);
		newElement.setOwner(DEFAULT_OWNER);
		parent.appendChild(newElement);
		return newElement;
	}

	/**
	 * Method get an array, contains 3 groups: categoryNum+level - parentNum - selfNum
	 * In this system, the calculation of the ID, there is a limit on the number of category (9 units) 
	 * and depth of nesting (10 levels)
	 * To empower inclusion of 4 elements in a group - store level separately from the number of category, 
	 * then both constraints will significantly expand
	 * @param num - number, ID of the parent element
	 * @return array with 3 elements
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
	
	/**
	 * Not implemented method, yet
	 */
	@Override
	public void editActivityTypeElement() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Method removes activity type from tree
	 * @see ActivityTypeProcessor#removeTreeElement(ActivityType, ActivityType)
	 */
	@Override
	public void removeActivityTypeElement(ActivityType activityType) {
		removeTreeElement(activityTypes, activityType);
	}
	
	/**
	 * Method removes removingType from the actual tree - parentType
	 * using a recursive traversal of the tree
	 * @see ActivityTypeProcessor#removeTreeElement(ActivityType, ActivityType)
	 */
	public void removeTreeElement(ActivityType parentType, ActivityType removingType){
		if(parentType.getActivityType().contains(removingType)){
			parentType.getActivityType().remove(removingType);
			return;
		}
		for (ActivityType t: parentType.getActivityType()){
			removeTreeElement(t, removingType);
		}
	}

	/**
	 * Observer method for receiving changes
	 * @param object - changes
	 */
	@Override
	public void update(Object object) {
		if(object.getClass() == ActivityType.class)
			this.activityTypes = (ActivityType) object;
	}

	/**
	 * Gets tree element by ID
	 * @see ActivityTypeProcessor#checkID(ActivityType, int)
	 * @param activityTypeID - the search element ID
	 * @return the search element
	 */
	public ActivityType getTreeElement(int activityTypeID) {
		ActivityType activityType = checkID(activityTypes, activityTypeID);
		return activityType;
	}
	
	/**
	 * Method finds the element using its ID
	 * Method use a recursive traversal of the tree
	 * @param activityType - the current top of the hierarchy
	 * @param activityTypeID - the search element ID
	 * @return founded ActivityType or null
	 */
	public ActivityType checkID(ActivityType activityType, int activityTypeID){
		if(activityType.getActivityTypeID()==activityTypeID){
			find = activityType;
		}
		for (ActivityType t: activityType.getActivityType()){
			checkID(t, activityTypeID);
		}
		return find;
	}

	/**
	 * Getter for the root of tree
	 * @return {@link ActivityTypeProcessor#activityTypes}
	 */
	public ActivityType getActivityTypes() {
		return activityTypes;
	}
}

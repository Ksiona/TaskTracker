package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.ActivityType;
import commonResources.model.ActivityTypeStat;
import commonResources.model.UserStat;

/**
 * @author Shmoylova Kseniya
 * Class defines the entity managing the retrieval, editing and storage of linked data
 * 
 * Retrieving:
 * 	statistic - deserialize the statistic of user from the binary files corresponding to the passed in query date interval:
 * 	activity type tree - deserialize tree from the XML representation using JAXB
 * 
 * Editing:
 * 	statistic is fully processed on the server side 
 * 	activity types tree server receives already formed
 * 
 * Storage:
 * 	statistics assumes the following structure:
 * folder with name = userName, containing files with names equal to the date save statistics,
 * files are binary;
 * 	activity types - XML
 */
public class VariableEssence implements IVariableEssence{
	
	private static final Logger log = Logger.getLogger(VariableEssence.class);
	private static final String STORAGE_PATH = "./target/classes/storage/";
	private static final String ACTIVITY_FILE_NAME = "activityTypes.xml";
	private static final String FILE_EXTENSION = ".bin";
	private static final String NEW_FOLDER_CREATED = " folder was created";
	private static final String SLASH = "/";
	private static final String STATUS_STATISTIC = " statistic saved";
	private static final String EMPTY_STRING = "";
	private static final long workDayStartTime = LocalTime.of(10, 0).toSecondOfDay();
	
	private ActivityType activityTypes;
	private WorkerThread workerThread;
	private Unmarshaller jaxbUnmarshaller;
	private Marshaller jaxbMarshaller;
	private DateTimeFormatter formatter;
	private boolean isFinded;
	private ActivityType notWorkActivityType;
	
	public VariableEssence(WorkerThread workerThread) {
		this.workerThread = workerThread;
		formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ActivityType.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbMarshaller = jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public VariableEssence(){
	}
	
	/**
	 * Method to restore the object from XML representation
	 * @exception IOException - output stream errors
	 * @exception JAXBException - unmarshaling errors
	 */
	@Override
	public ActivityType getActivityTypesTree() {
		if(activityTypes == null){
			try(InputStream fis = new FileInputStream(STORAGE_PATH + ACTIVITY_FILE_NAME)) {
				activityTypes = (ActivityType) jaxbUnmarshaller.unmarshal(fis);
			} catch (JAXBException | IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return activityTypes;
	}
	
	/**
	 * Method to write the XML representation of the activity types tree
	 * @exception IOException - output stream errors
	 * @exception JAXBException - marshaling errors
	 */
	@Override
	public void setActivityTypesTree(ActivityType activityType) {
		try(OutputStream outStream = new FileOutputStream(STORAGE_PATH+ACTIVITY_FILE_NAME)) {
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(activityType, outStream);
			activityTypes =null;
		} catch (JAXBException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Method to validate user registration
	 * @see WorkerThread#onUserConnection(String)
	 * @return true - user validate, false - otherwise
	 */
	@Override
	public boolean login(String userName) {
		return workerThread.onUserConnection(userName);
	}
	
	/**
	 * Method allows disconnected from the server in normal mode
	 * @see WorkerThread#onUserDisconnected(String)
	 */
	@Override
	public void disconnect(String userName) {
		workerThread.onUserDisconnected(userName);
	}
	
	/**
	 * Method checks existence statistic for this user on current date
	 * If exist, load it and merge with current 
	 * @see VariableEssence#mergeStat(UserStat, UserStat)
	 * otherwise - serialize statistics sent
	 * @param userName - registered name of user
	 * @param statistic - current statistics sent to save
	 * @exception IOException I/O streams errors - logfile only
	 * @exception ClassNotFoundException - deserialize errors
	 */
	@Override
	public void setUserStat(String userName, UserStat statistic) {
		String sDate = LocalDate.now().format(formatter);
		File userFolder = new File(STORAGE_PATH+userName+SLASH);
		if (userFolder.mkdir()) {
			log.info(userName+NEW_FOLDER_CREATED);
		}
		File statFile = new File(userFolder +SLASH +sDate+FILE_EXTENSION);
		if(statFile.exists()){
			try (ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(statFile));) {
				UserStat existStat = (UserStat) objectInStream.readObject();
				statistic = mergeStat(statistic, existStat);
				statFile.delete();
			} catch (IOException | ClassNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		}
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(
				new FileOutputStream(new File(userFolder +SLASH +sDate+FILE_EXTENSION)))){		
			objectOutStream.writeObject(statistic);
			log.info(userName+STATUS_STATISTIC);
		}catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * Method merges exist statistic with current 
	 * @param statistic - current statistics sent to save
	 * @param existStat - exist statistics from file
	 * @return - updated statistics, with new workEnd time
	 * @see VariableEssence#setNotWorkingActivity(UserStat, UserStat)
	 */
	private UserStat mergeStat(UserStat statistic, UserStat existStat) {
		try {
			UserStat temp = new UserStat(statistic.getUserName());
			for (ActivityTypeStat stat: statistic.getActivityStatList()){
				isFinded = false;
				for(ActivityTypeStat exist:existStat.getActivityStatList()){
					if (stat.getActivityTypeTitle().equalsIgnoreCase(exist.getActivityTypeTitle())){
						exist.setTimeInterval(exist.getTimeInterval()+stat.getTimeInterval());
						isFinded  = true;
					}
				}
				if(!isFinded)
					temp.getActivityStatList().add(stat);
			}
			existStat.getActivityStatList().addAll(temp.getActivityStatList());
		} finally{
			if (notWorkActivityType ==null)
				setNotWorkingActivity(existStat, statistic);
			existStat.setWorkEnd(statistic.getWorkEnd());
		}
		return existStat;
	}

	/**
	 * Method sum not working activity interval with time application was not active
	 * @param statistic - current statistics sent to save
	 * @param existStat - exist statistics from file
	 */
	private void setNotWorkingActivity(UserStat existStat, UserStat statistic) {
		long notWorkActivity = (statistic.getWorkStart()-existStat.getWorkEnd())+(existStat.getWorkStart()-workDayStartTime);
		existStat.setWorkStart(workDayStartTime);
		notWorkActivityType = 	new ActivityType("Not working", 100001);
		existStat.addActivity(LocalDate.now(), notWorkActivityType, notWorkActivity);
	}

	/**
	 * Method combines the statistics of multiple files, corresponding to the date interval
	 * 
	 * @see VariableEssence#getFiles(String, LocalDate, LocalDate)
	 * @see VariableEssence#combineStat(String, List)
	 * 
	 * @param userName - registered name of user
	 * @param start - start date of the requested interval
	 * @param end - last date of the requested interval
	 * @return UserStat object
	 */
	@Override
	public UserStat getUserStat(String userName, LocalDate start, LocalDate end) {
		List<UserStat> statList = new ArrayList<>();
		List<File> files = getFiles(userName, start, end);

		for(File file : files)
			try (ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(new File(file.getAbsolutePath())));) {
				statList.add((UserStat) objectInStream.readObject());
			} catch (ClassCastException | ClassNotFoundException | IOException e){
				log.warn(e.getMessage(), e);
			}
		return combineStat(userName,statList);
	}
	
	/**
	 * Method just sum {@link ActivityTypeStat} objects in the result UserStat object 
	 * @param userName - the user for which statistics were kept
	 * @param statList - the list of loaded statistical units
	 * @return UserStat object
	 */
	private UserStat combineStat(String userName, List<UserStat> statList) {
		UserStat statistic = new UserStat(userName);
		log.info(statistic.getActivityStatList().size());
		for(UserStat us:statList)
			statistic.getActivityStatList().addAll(us.getActivityStatList());
		return statistic;
	}
	
	/**
	 * @return list of files for loading statistic
	 */
	public List<File> getFiles(String userName, LocalDate start, LocalDate end){
		List<File> files = new ArrayList<>();
		File dir = new File(STORAGE_PATH+userName+SLASH);
		for(File file : dir.listFiles()){
			String fileName = file.getName().replaceAll(FILE_EXTENSION, EMPTY_STRING);
			if((file.getName().endsWith(FILE_EXTENSION))&& file.isFile()
					&& LocalDate.parse(fileName,formatter).isAfter(start.minusDays(1)) && LocalDate.parse(fileName,formatter).isBefore(end.plusDays(1))){
				files.add(file);
			}
		}
		return files;
	}
}

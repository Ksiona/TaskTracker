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

	@Override
	public boolean login(String userName) {
		return workerThread.onUserConnection(userName);
	}

	@Override
	public void disconnect(String userName) {
		workerThread.onUserDisconnected(userName);
	}
	
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

	private void setNotWorkingActivity(UserStat existStat, UserStat statistic) {
		long notWorkActivity = (statistic.getWorkStart()-existStat.getWorkEnd())+(existStat.getWorkStart()-workDayStartTime);
		existStat.setWorkStart(workDayStartTime);
		notWorkActivityType = 	new ActivityType("Not working", 100001);
		existStat.addActivity(LocalDate.now(), notWorkActivityType, notWorkActivity);
	}

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
		return mergeStat(userName,statList);
	}
	
	private UserStat mergeStat(String userName, List<UserStat> statList) {
		UserStat statistic = new UserStat(userName);
		for(UserStat us:statList)
			statistic.getActivityStatList().addAll(us.getActivityStatList());
		return statistic;
	}
	
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

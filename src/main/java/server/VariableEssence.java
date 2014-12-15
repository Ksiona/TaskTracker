package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.Task;
import commonResources.model.UserStat;

public class VariableEssence implements IVariableEssence{
	private static final String STORAGE_PATH = "./target/classes/storage/";
	private static final String ACTIVITY_FILE_NAME = "tasks.xml";
	private static final Logger log = Logger.getLogger(VariableEssence.class);
	private static final String FILE_EXTENSION = ".bin";
	private Task tasks;
	WorkerThread workerThread;
	private String userName;
	private int role;
	private JAXBContext jaxbContext;
	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	
	public VariableEssence(WorkerThread workerThread) {
		this.workerThread = workerThread;
		format = new SimpleDateFormat("dd.MM.yyyy");
		try {
			jaxbContext = JAXBContext.newInstance(Task.class);
		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		}
	}
	public VariableEssence(){
	}
	@Override
	public Task getTaskTree() {
		try {
			Unmarshaller um = jaxbContext.createUnmarshaller();
			tasks = (Task) um.unmarshal(new FileInputStream(STORAGE_PATH + ACTIVITY_FILE_NAME));
		} catch (FileNotFoundException | JAXBException e) {
			log.error(e.getMessage(), e);
		}
		return tasks;
	}
	
	@Override
	public void setTaskTree(Task task) {
		try {
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			OutputStream outStream = new FileOutputStream(STORAGE_PATH+ACTIVITY_FILE_NAME);
			jaxbMarshaller.marshal(task, outStream);
			outStream.close();
		} catch (JAXBException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean login(String userName) {
		this.userName = userName;
		return workerThread.onUserConnection(userName);
	}

	@Override
	public void disconnect(String userName) {
		workerThread.onUserDisconnected(userName);
	}
	@Override
	public void setUserStat(String userName, UserStat statistic) {
		String sDate = format.format(new Date(System.currentTimeMillis()));
		File userFolder = new File(STORAGE_PATH+userName+"/");
		if (userFolder.mkdir()) {
			log.info(userName+" folder was created");
		}
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(
				new FileOutputStream(new File(userFolder +"/" +sDate+FILE_EXTENSION)))){		
			objectOutStream.writeObject(statistic);
			log.info(userName+" stat saved");
		}catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	@Override
	public UserStat getUserStat(String userName, Date start, Date end) {
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
			for(int taskID:us.getActivityTypeList().keySet())
				statistic.addActivityType(taskID, us.getActivityTypeList().get(taskID));
		return statistic;
	}
	
	public List<File> getFiles(String userName, Date start, Date end){
		List<File> files = new ArrayList<>();
		File dir = new File(STORAGE_PATH+userName+"/");
		for(File file : dir.listFiles())
			try {
				if((file.getName().endsWith(FILE_EXTENSION))&& file.isFile()
						&& format.parse(file.getName()).compareTo(start)>=0 && format.parse(file.getName()).compareTo(end)<=0){
					files.add(file);
				}
			} catch (ParseException e) {
				log.warn(e.getMessage(), e);
			}
		return files;
	}
}

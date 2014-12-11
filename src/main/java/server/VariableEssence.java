package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.Task;

public class VariableEssence implements IVariableEssence{
	private static final String STORAGE_FILE_PATH = "./target/classes/storage/tasks.xml";
	private Task tasks;
	WorkerThread workerThread;
	private String userName;
	private int role;
	private JAXBContext jaxbContext;
	
	public VariableEssence(WorkerThread workerThread) {
		this.workerThread = workerThread;
		try {
			jaxbContext = JAXBContext.newInstance(Task.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	public VariableEssence(){
	}
	@Override
	public Task getTaskTree() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			tasks = (Task) um.unmarshal(new FileInputStream(STORAGE_FILE_PATH));
		} catch (FileNotFoundException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tasks;
	}
	
/*	@Override
	public Task getTreeElement(int taskID) {
		Task task = checkID(getTaskTree(), taskID);
		return task;
	}
	
	public Task checkID(Task task, int taskID){
		if(task.getTaskID()==taskID){
			System.out.println(task.getTaskID());
			find = task;
		}
		for (Task t: task.getTask()){
			checkID(t, taskID);
		}
		return find;
	}*/
	
	@Override
	public void setTaskTree(Task task) {
		try {
			writeXML(tasks);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeXML(Task task) throws JAXBException, IOException{
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		OutputStream outStream = new FileOutputStream(STORAGE_FILE_PATH);
		jaxbMarshaller.marshal(task, outStream);
		outStream.close();
	}

	@Override
	public void login(String userName, int role) {
		this.userName = userName;
		this.role = role;
		workerThread.onUserConnection(userName, role);
	}

	@Override
	public void disconnect() {
		workerThread.onUserDisconnected(userName);
	}

}
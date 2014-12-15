package logic;

import interfaces.ITaskProcessor;
import interfaces.Observer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import commonResources.model.Task;
import commonResources.model.UserStat;

public class TaskProcessor implements ITaskProcessor, Observer {

	private static final TaskProcessor INSTANCE = new TaskProcessor();
	private static final String FILE_EXTENSION = ".bin";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static final Logger log = Logger.getLogger(TaskProcessor.class);
	private Task tasks;
	private Task find;
	
	private TaskProcessor() {
	}
	
	public static TaskProcessor getInstance(){
		return INSTANCE;
	}
	
/*	private static final String DONT_WORKING = "Don't working";
	private static final String DINNER = "Dinner";
	private static final String REST = "Rest";
	private static final String ANOTHER_WORK = "Work is not associated with PC";
	
 	public static Task getDefaultTask() {
		Task task = new Task(DONT_WORKING, 1);
		task.appendChild(new Task(DINNER, 101000));
		task.appendChild(new Task(REST, 101001)); 
		task.appendChild(new Task(ANOTHER_WORK, 101002));
		return task;
	}*/

	public static void print(Task task){
		System.out.println(task);
		for (Task t: task.getTask())
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
	public void insertTaskElement() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void editTaskElement() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeTaskElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Task task) {
		this.tasks = task;
	}
	

	public Task getTreeElement(int taskID) {
		Task task = checkID(tasks, taskID);
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
	}
}

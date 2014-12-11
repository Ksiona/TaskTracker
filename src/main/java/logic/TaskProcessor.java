package logic;

import interfaces.ITaskProcessor;
import interfaces.Observer;
import commonResources.model.Task;

public class TaskProcessor implements ITaskProcessor, Observer {
	private static final String STORAGE_PATH = "./target/classes/storage/";

	private static final TaskProcessor INSTANCE = new TaskProcessor();
	private Task tasks;
	
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
	public void writeFileStat() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void readFileStat() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCurrentTaskElement() {
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
}

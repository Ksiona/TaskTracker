package commonResources.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement()
@XmlType(propOrder={FieldNames.TASK_NOTES, FieldNames.TASK})
public class Task implements Serializable {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String owner;
	private String taskTitle;
	private int taskID;
	private String taskNotes;
	private List<Task> task = new ArrayList<>();	
	
	public Task() {
	}
	
	public Task(String taskTitle, int taskID) {
		super();
		this.taskTitle = taskTitle;
		this.taskID = taskID;
	}
	
	public void appendChild(Task newTask){
		this.task.add(newTask);
	}
	
	public Task getChild(int taskID){
		return task.get(taskID);
	}
	@XmlAttribute
	public String getTaskTitle() {
		return taskTitle;
	}
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
	@XmlAttribute
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	public List<Task> getTask() {
		return task;
	}
	public void setTask(List<Task> task) {
		this.task = task;
	}
	public String getTaskNotes() {
		return taskNotes;
	}
	public void setTaskNotes(String taskNotes) {
		this.taskNotes = taskNotes;
	}
	@XmlAttribute
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return getTaskTitle();
	}
}

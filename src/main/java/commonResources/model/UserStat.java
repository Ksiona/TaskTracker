package commonResources.model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;


public class UserStat  extends Task<ObservableList<UserStat>>{

    private int pauseTime; // milliseconds
	private String taskTitle;
	private long timeStart;
	private int percent;

    public static final int NUM_ITERATIONS = 180;

    public UserStat(String taskTitle, long timeStart) {
      this.taskTitle = taskTitle;
      this.pauseTime = 1000;
      this.timeStart= timeStart;
    }

	public UserStat() {
	}
	
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}
	public String getTaskTitle() {
		return taskTitle;
	}
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
	
	/**
	 * method for use with JavaFX UI only
	 */
	@Override
    protected ObservableList<UserStat> call() throws Exception {
		this.updateTitle(taskTitle);;
		this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
        updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
        this.updateMessage((100.0 * i) / ((System.currentTimeMillis()- timeStart)/1000)+"%");
        Thread.sleep(pauseTime);
      }
      this.updateProgress(1, 1);
      
      return null;
    }
}
package ui.control;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;


public class ActivityTypeProgress  extends Task<ObservableList<ActivityTypeProgress>>{
	
	//10 hour work day in seconds
	private static final int NUM_ITERATIONS = 36000;
	private static final int PAUSE_INTERVAL_MS = 1000;
	private static final String DESCRIPTION_SECONDS = " s";
	private static final String DESCRIPTION_MINUTES = " min";
	private String activityTypeTitle;
	private int secondsElapsed;
	private boolean isSuspended;
	
	public ActivityTypeProgress(String activityTypeTitle) {
		this.setActivityTypeTitle(activityTypeTitle);
	}
	
	public String getActivityTypeTitle() {
		return activityTypeTitle;
	}
	public void setActivityTypeTitle(String activityTypeTitle) {
		this.activityTypeTitle = activityTypeTitle;
	}
	public boolean isSuspended() {
		return isSuspended;
	}
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}
	public int getSecondsElapsed() {
		return secondsElapsed;
	}
	public void setSecondsElapsed(int secondsElapsed) {
		this.secondsElapsed = secondsElapsed;
	}
	
	public void getFormattedTime(int i){
		if(i<60)
			this.updateMessage(i+DESCRIPTION_SECONDS);
		else if(i>=60 && i<3600)
			this.updateMessage((int)i/60 +DESCRIPTION_MINUTES);
		else{
			int hours = (int)i/3600;
			int min = (i%3600)/60;
			this.updateMessage(hours + ":" + min + DESCRIPTION_MINUTES);
		}
	}
	
	/**
	 * 
	 */
	@Override
	protected ObservableList<ActivityTypeProgress> call() throws Exception {
		this.updateTitle(activityTypeTitle);
		this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
		for (int i = 0; i <= NUM_ITERATIONS; i++) {
			while (isSuspended()) {
				Thread.yield();
			}
			updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
			getFormattedTime(i);
			setSecondsElapsed(i);
			Thread.sleep(PAUSE_INTERVAL_MS);
		}
		this.updateProgress(1, 1);
		getFormattedTime(getSecondsElapsed());
		
		return null;
	}
}

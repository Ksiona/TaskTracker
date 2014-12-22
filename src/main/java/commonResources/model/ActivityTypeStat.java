package commonResources.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @author Shmoylova Kseniya
 * POJO for the base component of entity "User statistic"
 */
public class ActivityTypeStat implements Serializable{
	
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private int activityTypeID;
	private String activityTypeTitle;
	private long timeInterval;
	private double percent;
	
	public ActivityTypeStat(LocalDate date, ActivityType activityType, long timeInterval) {
		this.date = date;
		this.setActivityTypeID(activityType.getActivityTypeID());
		this.activityTypeTitle = activityType.getActivityTypeTitle();
		this.timeInterval = timeInterval;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getActivityTypeTitle() {
		return activityTypeTitle;
	}

	public void setActivityTypeTitle(String activityTypeTitle) {
		this.activityTypeTitle = activityTypeTitle;
	}

	public int getActivityTypeID() {
		return activityTypeID;
	}

	public void setActivityTypeID(int activityTypeID) {
		this.activityTypeID = activityTypeID;
	}

	public long getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}

	public double getPercent() {
		return new BigDecimal(percent).setScale(2, RoundingMode.UP).doubleValue();
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

}
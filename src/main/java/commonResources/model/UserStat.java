package commonResources.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shmoylova Kseniya
 * POJO for the entity "User statistic"
 */
public class UserStat implements Serializable{

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private long workStart;
	private long workEnd;
	private List<ActivityTypeStat> activityStatList;
	
	public UserStat(String userName) {
		this.setUserName(userName);
		activityStatList = new ArrayList<>();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getWorkStart() {
		return workStart;
	}

	public void setWorkStart(long workStart) {
		this.workStart = workStart;
	}

	public long getWorkEnd() {
		return workEnd;
	}

	/**
	 * Method sets the time the application is closed
	 * and computes for each task, the percentage of total time.
	 * @param workEnd - time converted into long value
	 */
	public void setWorkEnd(long workEnd) {
		this.workEnd = workEnd;
		for(ActivityTypeStat asr:activityStatList){
			double percent = (double)asr.getTimeInterval()*100/(workEnd - workStart);
			asr.setPercent(percent);
		}
	}

	public List<ActivityTypeStat> getActivityStatList() {
		return activityStatList;
	}

	public void setActivityStatList(List<ActivityTypeStat> activityStatList) {
		this.activityStatList = activityStatList;
	}
	
	/**
	 * Method sets new activityTypeStat if not in the list {@link UserStat#activityStatList} 
	 * or sets new time interval in the opposite case
	 * @param workEnd - time converted into long value
	 */
	public void addActivity(LocalDate date, ActivityType activityType, long timeInterval){
		boolean isFinded = false;
		for(ActivityTypeStat asr:activityStatList){
			if (asr.getActivityTypeTitle().equalsIgnoreCase(activityType.getActivityTypeTitle())){
				asr.setTimeInterval(timeInterval);
				isFinded  = true;
			}
		}
		if(!isFinded)
			activityStatList.add(new ActivityTypeStat(date, activityType, timeInterval));
	}
}

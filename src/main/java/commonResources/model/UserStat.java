package commonResources.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserStat implements Serializable{

	/**
	 * default Id
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

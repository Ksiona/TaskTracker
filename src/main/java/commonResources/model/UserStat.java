package commonResources.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserStat implements Serializable{

	/**
	 * default Id
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private long workStart;
	private long workEnd;
	private Map<Integer, Long> activityTypeList;
	
	public UserStat(String userName) {
		this.setUserName(userName);
		activityTypeList = new HashMap<>();
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
	}

	public long getTimeInterval(int activityTypeID) {
		return activityTypeList.get(activityTypeID);
	}

	public Map<Integer, Long> getActivityTypeList() {
		return activityTypeList;
	}

	public void setActivityTypeList(Map<Integer, Long> activityTypeList) {
		this.activityTypeList = activityTypeList;
	}
	
	public void setActivityType(int activityTypeID, long timeInterval) {
		if(activityTypeList.containsKey(activityTypeID))
			activityTypeList.replace(activityTypeID, getTimeInterval(activityTypeID), timeInterval);
		else
			activityTypeList.put(activityTypeID, timeInterval);
	}
	
	public void sumActivity(int activityTypeID, long timeInterval) {
		if(activityTypeList.containsKey(activityTypeID))
			activityTypeList.replace(activityTypeID, getTimeInterval(activityTypeID),  getTimeInterval(activityTypeID)+timeInterval);
		else
			activityTypeList.put(activityTypeID, timeInterval);
	}
}

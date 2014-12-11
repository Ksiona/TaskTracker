package commonResources.model;

import java.io.Serializable;
import java.util.Date;

public class TrackerUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int ROLE_ADMIN = 0;
	private static final int ROLE_USER = 1;
	private String userName;
	private int role;
	private UserStat statistic;
	
	public TrackerUser(String userName, int role) {
		this.userName = userName;
		this.role = role;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public UserStat getStatistic(Date date) {
		return statistic;
	}
	public void setStatistic(UserStat statistic) {
		this.statistic = statistic;
	}
}

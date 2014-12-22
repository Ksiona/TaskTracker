package commonResources.model;

import java.io.Serializable;

/**
 * @author Shmoylova Kseniya
 * POJO for entity "User"
 */
public class TrackerUser implements Serializable{

	private static final long serialVersionUID = 1L;
	private String userName;;
	
	public TrackerUser(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}

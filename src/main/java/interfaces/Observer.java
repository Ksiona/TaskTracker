package interfaces;

import commonResources.model.ActivityType;

public interface Observer {
	 
	//method to update the observer, used by subject
	void update(ActivityType activityType);

}

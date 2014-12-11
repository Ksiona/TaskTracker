package interfaces;

import commonResources.model.Task;

public interface Observer {
	 
	//method to update the observer, used by subject
	void update(Task task);

}

package commonResources.interfaces;

import commonResources.model.Task;

public interface IVariableEssence {
	public void login(String userName, int role);
    public Task getTaskTree();
    public void setTaskTree(Task task);
    public void disconnect();
}

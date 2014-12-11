package interfaces;

import commonResources.model.Task;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public interface IViewMediator {
	public void WidgetChanged(IViewColleague col, Object changes);

	public TreeView getTreeView();

	public void setStatisticPaneElement(TreeItem<Task> newVal);
}

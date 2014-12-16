package interfaces;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Window;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public interface IViewMediator {
	public void WidgetChanged(IViewColleague col, Object changes);

	public TreeView getTreeView();
	public String getUserName();
	public void setStatisticPaneElement(TreeItem<ActivityType> newVal);
	public void login();
	public Window getWindow();
	public UserStat getStatistic();
	public void createTabPane();
}

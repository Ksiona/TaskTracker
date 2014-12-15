package interfaces;

import commonResources.model.Task;
import commonResources.model.UserStat;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public interface IViewMediator {
	public void WidgetChanged(IViewColleague col, Object changes);

	public TreeView getTreeView();
	public String getUserName();
	public void setStatisticPaneElement(TreeItem<Task> newVal);
	public void login();
	public Window getWindow();
	public UserStat getStatistic();
	public void createStatStage();
}

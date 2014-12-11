package ui.control;

import java.util.ArrayList;
import java.util.List;

import interfaces.IViewColleague;
import interfaces.IViewMediator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import commonResources.model.Task;
import commonResources.model.UserStat;

public class StatPane extends TableView<UserStat> implements IViewColleague{
	
	private static final String COL_TITLE = "title";
	private static final String COL_PROGRESS = "progress";
	private static final String COL_STATUS = "Status";
	private static final String TASK_PROPERTY_VALUE = "message";
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	private IViewMediator mainFrame;
	private List<String> statList;
	private long timeStart;
	
	public StatPane(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		statList = new ArrayList<>();
		init();
	}

	@SuppressWarnings(SUPPRESS_UNCHECKED)
	public void init(){
		//this.getItems().add(new UserStat("Dinner"));
	    
	    TableColumn<UserStat, String> titleCol = new TableColumn<>(COL_TITLE);
	    titleCol.setCellValueFactory(new PropertyValueFactory<UserStat, String>(COL_TITLE));
	    titleCol.setPrefWidth(150);

	    TableColumn<UserStat, Double> progressCol = new TableColumn<>(COL_PROGRESS);
	    progressCol.setCellValueFactory(new PropertyValueFactory<UserStat, Double>(COL_PROGRESS));
	    progressCol.setCellFactory(ProgressBarTableCell.<UserStat> forTableColumn());
	    progressCol.setPrefWidth(336);
	    
	    TableColumn<UserStat, String> statusCol = new TableColumn<>(COL_STATUS);
	    statusCol.setCellValueFactory(new PropertyValueFactory<UserStat, String>(TASK_PROPERTY_VALUE));
	    statusCol.setPrefWidth(50);

	    this.getColumns().addAll(titleCol, progressCol, statusCol);
		
	}
	/*private void showReport(Node node) {
		view.getChildren().clear();
        Button tb1 = new Button("Day report");
        Button tb2 = new Button("Week report");
        Button tb3 = new Button("Month report");
        Button tb4 = new Button("Close report");
        tb4.setOnMouseClicked(event -> changed(null));

        GridPane.setConstraints(tb1,0,1);
        GridPane.setConstraints(tb2,0,2);
        GridPane.setConstraints(tb3,0,3);
        GridPane.setConstraints(tb4,0,9);
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(10);

        grid.getChildren().addAll(tb1, tb2, tb3, tb4);
		//b.setOnMouseClicked(event ->  editTask());
		view.setCenter(node);
		view.setRight(grid);
		changed(view);
	}
	*/

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
		
	}

	//TODO not finished
	public void addElement(TreeItem<Task> newVal) {
		String title = newVal.getValue().getTaskTitle();
		if(statList.isEmpty())
			timeStart=System.currentTimeMillis();
		if(!statList.contains(title)){
			statList.add(title);
			UserStat current = new UserStat(title, timeStart);
			this.getItems().add(current);
			new Thread(current).start();
		}
	}
}

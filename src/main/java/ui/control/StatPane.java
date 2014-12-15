package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import org.apache.log4j.Logger;

import commonResources.model.Task;

public class StatPane extends TableView<ProgressCellModel> implements IViewColleague{
	
	private static final Logger log = Logger.getLogger(StatPane.class);
	private static final String COL_TITLE = "title";
	private static final String COL_PROGRESS = "progress";
	private static final String COL_STATUS = "Status";
	private static final String TASK_PROPERTY_VALUE = "message";
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	private static final String LOAD = "Load report";
	private static final String CLOSE = "Close report";
	private IViewMediator mainFrame;
	private List<String> statList;
	private long timeStart;
	private List<Thread> threadList;
	
	public StatPane(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		statList = new ArrayList<>();
		threadList = new ArrayList<>();
		init();
	}

	public StatPane() {
		initReport();
	}

	@SuppressWarnings(SUPPRESS_UNCHECKED)
	public void init(){
	    TableColumn<ProgressCellModel, String> titleCol = new TableColumn<>(COL_TITLE);
	    titleCol.setCellValueFactory(new PropertyValueFactory<ProgressCellModel, String>(COL_TITLE));
	    titleCol.setPrefWidth(150);

		TableColumn<ProgressCellModel, Double> progressCol = new TableColumn<>(COL_PROGRESS);
		progressCol.setCellValueFactory(new PropertyValueFactory<ProgressCellModel, Double>(COL_PROGRESS));
		progressCol.setCellFactory(ProgressBarTableCell.<ProgressCellModel> forTableColumn());
		progressCol.setPrefWidth(300);
	    
	    TableColumn<ProgressCellModel, String> statusCol = new TableColumn<>(COL_STATUS);
	    statusCol.setCellValueFactory(new PropertyValueFactory<ProgressCellModel, String>(TASK_PROPERTY_VALUE));
	    statusCol.setPrefWidth(86);

	    this.getColumns().addAll(titleCol, progressCol, statusCol);
	}
	
	private void initReport() {
		// TODO Auto-generated method stub
		
	}
	public Node showReport(String userName) {
		BorderPane borderPane = new BorderPane();
		DatePicker datePicker = new DatePicker();

        Button tb1 = new Button(LOAD);
        Button tb2 = new Button(CLOSE);
     //   tb2.setOnMouseClicked(event -> changed(null));

        GridPane.setConstraints(datePicker,1,0);

        GridPane.setConstraints(tb1,20,0);
        GridPane.setConstraints(tb2,23,0);
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(10);

        grid.getChildren().addAll(datePicker,tb1, tb2);
        borderPane.setCenter(this);
        borderPane.setBottom(grid);
		return borderPane;
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}

	//TODO not finished, ask about deprecation methods
	public void addElement(TreeItem<Task> newVal) {
		String title = null;
		try{
			title = newVal.getValue().getTaskTitle();
		}catch (NullPointerException e){
			log.warn(e.getMessage(), e);
		}
		if(statList.isEmpty())
			timeStart=System.currentTimeMillis();

		if(!statList.contains(title)){
			statList.add(title);
			ProgressCellModel current = new ProgressCellModel(title, timeStart);
			this.getItems().add(current);
			Thread thread = new Thread(current);
			threadList.add(thread);
			thread.start();
			for (Thread t:threadList)
				if(t != thread)
					t.suspend();
		}
	}
}

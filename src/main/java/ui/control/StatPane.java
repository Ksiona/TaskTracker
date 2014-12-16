package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import org.apache.log4j.Logger;

import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public class StatPane extends TableView<ActivityTypeProgress> implements IViewColleague{
	
	private static final Logger log = Logger.getLogger(StatPane.class);
	private static final String COL_ACTIVITY = "Activity";
	private static final String TASK_PROPERTY_TITLE = "title";
	private static final String COL_PROGRESS = "Progress";
	private static final String TASK_PROPERTY_PROGRESS = "progress";
	private static final String COL_TIME = "Time";
	private static final String TASK_PROPERTY_VALUE = "message";
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	private IViewMediator mainFrame;
	private List<String> statList;
	private UserStat statistic;
	private Map<Thread, ActivityTypeProgress> threadList;
	
	public StatPane(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		statList = new ArrayList<>();
		threadList = new HashMap<>();
		init();
	}

	public StatPane() {
		initReport();
	}

	@SuppressWarnings(SUPPRESS_UNCHECKED)
	public void init(){
	    TableColumn<ActivityTypeProgress, String> titleCol = new TableColumn<>(COL_ACTIVITY);
	    titleCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeProgress, String>(TASK_PROPERTY_TITLE));
	    titleCol.setPrefWidth(150);

		TableColumn<ActivityTypeProgress, Double> progressCol = new TableColumn<>(COL_PROGRESS);
		progressCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeProgress, Double>(TASK_PROPERTY_PROGRESS));
		progressCol.setCellFactory(ProgressBarTableCell.<ActivityTypeProgress> forTableColumn());
		progressCol.setPrefWidth(300);
	    
	    TableColumn<ActivityTypeProgress, String> statusCol = new TableColumn<>(COL_TIME);
	    statusCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeProgress, String>(TASK_PROPERTY_VALUE));
	    statusCol.setPrefWidth(86);

	    this.getColumns().addAll(titleCol, progressCol, statusCol);
	}
	
	private void initReport() {
		// TODO Auto-generated method stub
		
	}
	
	public Node showReport(String userName) {
		BorderPane borderPane = new BorderPane();
		return borderPane;
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}

	
	public void addElement(TreeItem<ActivityType> newVal) {
		String title = null;
		try{
			title = newVal.getValue().getActivityTypeTitle();

			if(statList.isEmpty()){
				statistic = new UserStat(mainFrame.getUserName());
				statistic.setWorkStart(System.currentTimeMillis());
			}
	
			if(!statList.contains(title)){
				statList.add(title);
				ActivityTypeProgress current = new ActivityTypeProgress(title);
				this.getItems().add(current);
				Thread thread = new Thread(current);
				thread.setDaemon(true);
				threadList.put(thread, current);
				thread.start();
				for (Thread t:threadList.keySet())
					if(t != thread)
						threadList.get(t).setSuspended(true);
			}
			else{
				for (Thread t:threadList.keySet()){
					ActivityTypeProgress pcm = threadList.get(t);
					if(pcm.getActivityTypeTitle().equalsIgnoreCase(title))
						pcm.setSuspended(false);
					else
						pcm.setSuspended(true);
				}
			}
			for (Thread t:threadList.keySet())
				statistic.setActivityType(newVal.getValue().getActivityTypeID(), threadList.get(t).getSecondsElapsed());
		}catch (NullPointerException e){
			log.warn(e.getMessage(), e);
		}
	}

	public UserStat getStatistic() {
		statistic.setWorkEnd(System.currentTimeMillis());
		return statistic;
	}
}

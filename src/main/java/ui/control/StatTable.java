package ui.control;

import interfaces.IViewMediator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import org.apache.log4j.Logger;

import commonResources.model.ActivityTypeStat;
import commonResources.model.ActivityType;
import commonResources.model.UserStat;

public class StatTable extends TableView<ActivityTypeProgress>{
	
	private static final Logger log = Logger.getLogger(StatTable.class);
	private static final String COL_ACTIVITY = "Activity";
	private static final String TASK_PROPERTY_TITLE = "title";
	private static final String COL_PROGRESS = "Progress";
	private static final String TASK_PROPERTY_PROGRESS = "progress";
	private static final String COL_TIME = "Time";
	private static final String TASK_PROPERTY_MESSAGE = "message";
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	private IViewMediator mainFrame;
	private List<Integer> statList;
	private UserStat statistic;
	private Map<Thread, ActivityTypeProgress> threadList;
	
	public StatTable(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		statList = new ArrayList<>();
		threadList = new HashMap<>();
		init();
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
	    statusCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeProgress, String>(TASK_PROPERTY_MESSAGE));
	    statusCol.setPrefWidth(86);

	    this.getColumns().addAll(titleCol, progressCol, statusCol);
	}

	public void addElement(TreeItem<ActivityType> newVal) {
		int itemID = 0;
		try{
			itemID = newVal.getValue().getActivityTypeID();

			if(statList.isEmpty()){
				statistic = new UserStat(mainFrame.getUserName());
				statistic.setWorkStart(System.currentTimeMillis()/1000);
			}
	
			if(!statList.contains(itemID)){
				statList.add(itemID);
				ActivityTypeProgress current = new ActivityTypeProgress(newVal.getValue());
				this.getItems().add(current);
				Thread thread = new Thread(current);
				thread.setDaemon(true);
				threadList.put(thread, current);
				thread.start();
			}
			for (Thread t:threadList.keySet()){
				statistic.addActivity(LocalDate.now(), newVal.getValue(), threadList.get(t).getSecondsElapsed());
				ActivityTypeProgress pcm = threadList.get(t);
				if(pcm.getActivityTypeID() == itemID)
					pcm.setSuspended(false);
				else
					pcm.setSuspended(true);
			}
		}catch (NullPointerException e){
			log.warn(e.getMessage(), e);
		}
	}

	public UserStat getStatistic() {
		for (Thread t:threadList.keySet()){
			ActivityTypeProgress pcm = threadList.get(t);
			for(ActivityTypeStat asr:statistic.getActivityStatList()){
				if (asr.getActivityTypeID() == pcm.getActivityTypeID()){
					asr.setTimeInterval(pcm.getSecondsElapsed());
				}
			}
		}
		statistic.setWorkEnd(System.currentTimeMillis()/1000);
		return statistic;
	}
}

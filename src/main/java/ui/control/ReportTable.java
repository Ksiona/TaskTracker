package ui.control;

import interfaces.IControllerViewMediator;
import interfaces.IViewColleague;
import interfaces.IViewMediator;
import interfaces.Observer;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.mediator.ControllerViewMediator;
import commonResources.model.ActivityTypeStat;
import commonResources.model.UserStat;

public class ReportTable extends TableView<ActivityTypeStat> implements IViewColleague, Observer{
	
	private static final String COL_ACTIVITY = "Activity";
	private static final String COL_DATE = "Date";
	private static final String COL_TIME = "Worked";
	private static final String COL_PERCENT = "%";
	private static final String PROPERTY_DATE = "date";
	private static final String PROPERTY_TITLE = "activityTypeTitle";
	private static final String PROPERTY_TIME = "timeInterval";
	private static final String PROPERTY_PERCENT = "percent";
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	private IViewMediator mainFrame;
	private IControllerViewMediator em;
	private ObservableList<ActivityTypeStat> data = FXCollections.observableArrayList();

	public ReportTable(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		em = ControllerViewMediator.getInstance();
		em.register(this);
		init();
	}

	@SuppressWarnings(SUPPRESS_UNCHECKED)
	private void init() {
	    TableColumn<ActivityTypeStat, LocalDate> dateCol = new TableColumn<>(COL_DATE);
	    dateCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeStat, LocalDate>(PROPERTY_DATE));
	    dateCol.setPrefWidth(120);

		TableColumn<ActivityTypeStat, String> titleCol = new TableColumn<>(COL_ACTIVITY);
		titleCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeStat, String>(PROPERTY_TITLE));
		titleCol.setPrefWidth(200);
	    
	    TableColumn<ActivityTypeStat, Long> timeCol = new TableColumn<>(COL_TIME);
	    timeCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeStat, Long>(PROPERTY_TIME));
	    timeCol.setPrefWidth(120);
	    
	    TableColumn<ActivityTypeStat, Double> percentCol = new TableColumn<>(COL_PERCENT);
	    percentCol.setCellValueFactory(new PropertyValueFactory<ActivityTypeStat, Double>(PROPERTY_PERCENT));
	    percentCol.setPrefWidth(100);

	    getColumns().addAll(dateCol, titleCol, timeCol, percentCol);
	}
	
	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}
	
	@Override
	public void update(Object object) {
		this.getItems().clear();
		if(object.getClass() == UserStat.class){
			for(ActivityTypeStat activityStat:((UserStat) object).getActivityStatList())
				data.add(activityStat);
			this.setItems(data);
			changed(this);
		}
	}
}

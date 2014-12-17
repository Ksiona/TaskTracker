package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.apache.log4j.Logger;

import ui.mediator.ControllerViewMediator;

public class ToolBarStat extends ToolBar implements IViewColleague{
	
	private static final Logger log = Logger.getLogger(ToolBarStat.class);
	private static final String ICON_PATH = "./src/main/resources/img/stat_tools/icon-";
	private static final String FILE_EXTENSION = ".png";
	private static final String TITLE_OPEN_DIALOG = "Select file to open";
	private static final String EXTENSION_DESCRIPTION = "Binary";
	private static final String EXTENSION_OPEN_DIALOG = "*.bin";
	private static final String TITLE_DIRECTORY_CHOOSER = "Select Output Directory";
	private static final String SYSTEM_USER_DIR = "user.dir";
	private BorderPane view;
	private ControllerViewMediator em;
	private IViewMediator mainFrame;
	private DatePicker startDatePicker;
	private DatePicker endDatePicker;
	private static final int n=3;
	private String anotherUserName;

	private Button[] buttons;
	
	public ToolBarStat(IViewMediator mainFrame, boolean modeState)  {
		this.mainFrame = mainFrame;
		view = new BorderPane();
		em = ControllerViewMediator.getInstance();
		buttons = new Button[n];
			initTools(modeState);
	}
	
	public void initTools(boolean modeState){
		this.getItems().clear();
		try{
			for (int i = 0; i < n; i++) {
			    buttons[i] = new Button("",new ImageView(new Image(new FileInputStream(ICON_PATH + i + FILE_EXTENSION))));
			    this.getItems().add(buttons[i]);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		endDatePicker = setEndDatePicker();
		startDatePicker = new DatePicker();
		startDatePicker.setPrefWidth(100);
		
		ComboBox<String> userSelector = new ComboBox<>();
		userSelector.setItems(FXCollections.observableArrayList("Me","Michael"));
		userSelector.setVisible(false);
		userSelector.setOnAction(event -> anotherUserName = userSelector.getValue());

		//TODO
		if(modeState){
			endDatePicker.setVisible(true);
			userSelector.setVisible(true);
			buttons[2].setOnMouseClicked(event -> loadReport(anotherUserName, startDatePicker.getValue(), endDatePicker.getValue()));
		}
		else{
			buttons[2].setOnMouseClicked(event -> loadReport(mainFrame.getUserName(), startDatePicker.getValue(), startDatePicker.getValue()));
		}
		buttons[0].setOnMouseClicked(event -> writeFileStat());
		buttons[1].setOnMouseClicked(event -> readFileStat());
		this.getItems().addAll(startDatePicker, endDatePicker, userSelector);
	}
	
	private void loadReport(String userName, LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null){
			em.loadStat(userName, startDate, endDate);
			changed(null);
		} else{
			//TODO Notify user about null in picker
		}
	}

	public void setModeState(boolean modeState) {
		initTools(modeState);
	}

	public DatePicker setEndDatePicker() {
		DatePicker datePicker2 = new DatePicker();
		datePicker2.setPrefWidth(100);
		datePicker2.setVisible(false);
		return datePicker2;
	}
	
	private void readFileStat() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle(TITLE_OPEN_DIALOG);
		 fileChooser.getExtensionFilters().addAll(
				 new ExtensionFilter(EXTENSION_DESCRIPTION, EXTENSION_OPEN_DIALOG));
		 File selectedFile = fileChooser.showOpenDialog(mainFrame.getWindow());
		 if (selectedFile != null) {
			em.readFileStat(selectedFile);
			//TODO statistic window
		 }
	}

	private void writeFileStat() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle(TITLE_DIRECTORY_CHOOSER);
		directoryChooser.setInitialDirectory(new File(System.getProperty(SYSTEM_USER_DIR)));
		File selectedDir = directoryChooser.showDialog(mainFrame.getWindow());
		if (selectedDir != null) {
			em.writeFileStat(selectedDir, mainFrame.getStatistic());
		}
	}
	
	private void changeView(Node node) {
		view.setCenter(node);
		changed(view);
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}
}

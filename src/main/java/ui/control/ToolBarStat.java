package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
	private static final int n=3;

	private Button[] buttons;
	
	public ToolBarStat(IViewMediator mainFrame, boolean modeState)  {
		this.mainFrame = mainFrame;
		view = new BorderPane();
		em = ControllerViewMediator.getInstance();
		buttons = new Button[n];
		//if(!modeState)
			initTools();
	}
	
	public void initTools(){
		try{
			for (int i = 0; i < n; i++) {
			    buttons[i] = new Button("",new ImageView(new Image(new FileInputStream(ICON_PATH + i + FILE_EXTENSION))));
			    this.getItems().add(buttons[i]);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		DatePicker datePicker = new DatePicker();
		//TODO 2 date pickers for manager mode
		buttons[0].setOnMouseClicked(event -> writeFileStat());
		buttons[1].setOnMouseClicked(event -> readFileStat());
	//	buttons[2].setOnMouseClicked(event -> em.loadStat(mainFrame.getUserName(), datePicker.getValue(), datePicker.getValue()));
		//TODO LocalDate to Date
		this.getItems().add(datePicker);
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

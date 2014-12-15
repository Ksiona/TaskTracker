package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ui.mediator.ControllerViewMediator;
import commonResources.model.Task;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ToolBarFX extends ToolBar implements IViewColleague{
	
	private static final Logger log = Logger.getLogger(ToolBarFX.class);
	private static final String ICON_PATH = "./src/main/resources/img/icon-";
	private static final String FILE_EXTENSION = ".png";
	private static final String TITLE_OPEN_DIALOG = "Select file to open";
	private static final String EXTENSION_DESCRIPTION = "Binary";
	private static final String EXTENSION_OPEN_DIALOG = "*.bin";
	private static final String TITLE_DIRECTORY_CHOOSER = "Select Output Directory";
	private static final String SYSTEM_USER_DIR = "user.dir";
	private BorderPane view;
	private ControllerViewMediator em;
	private IViewMediator mainFrame;
	private TreeView treeView;
	private static final int n=8;

	private Button[] buttons;
	
	public ToolBarFX(IViewMediator mainFrame, boolean modeState)  {
		this.mainFrame = mainFrame;
		view = new BorderPane();
		em = ControllerViewMediator.getInstance();
		buttons = new Button[n];
		if(!modeState)
			initUserTools();
		else
			initManagerTools();
	}
	
	public void initUserTools(){
		try{
			for (int i = 3; i < n; i++) {
			    buttons[i] = new Button("",new ImageView(new Image(new FileInputStream(ICON_PATH + i + FILE_EXTENSION))));
			    this.getItems().add(buttons[i]);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		buttons[3].setOnMouseClicked(event -> writeFileStat());
		buttons[4].setOnMouseClicked(event -> readFileStat());
		buttons[5].setOnMouseClicked(event -> changeView(hideNotMine(getTreeNode().getRoot())));
		buttons[6].setOnMouseClicked(event -> changeView(expandAll(getTreeNode().getRoot())));
		buttons[7].setOnMouseClicked(event -> em.loadTasks());
		
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

	public void initManagerTools(){
		try{
			for (int i = 0; i < 3; i++) {
			    buttons[i] = new Button("",new ImageView(new Image(new FileInputStream(ICON_PATH + i + FILE_EXTENSION))));
			    this.getItems().add(buttons[i]);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		buttons[0].setOnMouseClicked(event -> em.insertTaskElement());
		buttons[1].setOnMouseClicked(event -> em.editTaskElement());
		buttons[2].setOnMouseClicked(event -> em.removeTaskElement());
		
		initUserTools();
	}
	
	public TreeView getTreeNode() {
		try{
			treeView = mainFrame.getTreeView();
		} catch (NullPointerException e){
			log.error(e.getMessage(), e);
		}
		return treeView;
	}
	
	private Node expandAll(TreeItem treeItem) {
		treeItem.setExpanded(true);
		for(Object ti:treeItem.getChildren()){
			 expandAll((TreeItem) ti);
		}
		return treeView;
	}

	private Node hideNotMine(TreeItem<Task> treeItem) {
		// TODO Auto-generated method stub
		return treeView;
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

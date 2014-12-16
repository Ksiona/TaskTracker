package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import org.apache.log4j.Logger;

import ui.mediator.ControllerViewMediator;

import commonResources.model.ActivityType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ToolBarTree extends ToolBar implements IViewColleague{
	
	private static final Logger log = Logger.getLogger(ToolBarTree.class);
	private static final String ICON_PATH = "./src/main/resources/img/tree_tools/icon-";
	private static final String FILE_EXTENSION = ".png";

	private BorderPane view;
	private ControllerViewMediator em;
	private IViewMediator mainFrame;
	private TreeView treeView;
	private static final int n=6;

	private Button[] buttons;
	
	public ToolBarTree(IViewMediator mainFrame, boolean modeState)  {
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
		buttons[3].setOnMouseClicked(event -> changeView(hideNotMine(getTreeNode().getRoot())));
		buttons[4].setOnMouseClicked(event -> changeView(expandAll(getTreeNode().getRoot())));
		buttons[5].setOnMouseClicked(event -> em.loadActivityTypes());
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
		buttons[0].setOnMouseClicked(event -> em.insertActivityTypeElement());
		buttons[1].setOnMouseClicked(event -> em.editActivityTypeElement());
		buttons[2].setOnMouseClicked(event -> em.removeActivityTypeElement());
		
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

	private Node hideNotMine(TreeItem<ActivityType> treeItem) {
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

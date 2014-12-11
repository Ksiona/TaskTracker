package ui.view;

	/**
	 * Frame:0 - main window 
	 */

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.awt.Toolkit;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.control.MenuFX;
import ui.control.StatPane;
import ui.control.ToolBarFX;
import ui.control.TreeViewFX;

public class MainFrameFX extends Application implements IViewMediator{
	private static final String APPLICATION_TITLE = "Task tracker";
	private static final String LABEL_DAILY_STAT = "Daily statistic";
	private static double SRC_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static double SRC_HEIGTH = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	private static double WIN_WIDTH = 800;
	private static double WIN_HEIGTH = 400;
	private GridPane pane;
	private StackPane stackpane;
	private final Scene scene;
	private TreeViewFX treeView;
	private TreeViewFX treeViewManager;
	private ToolBarFX toolbarUser;
	private ToolBarFX toolbarManager;
	private MenuFX menuBar;
	private VBox tools;
	private StackPane statisticPane;
	private StatPane tableView;
	private boolean modeState =false;
	
	public MainFrameFX() {
		pane = new GridPane();
		scene = new Scene(pane, WIN_WIDTH, WIN_HEIGTH, Color.ALICEBLUE);
		stackpane = new StackPane();
		stackpane.setAlignment(Pos.TOP_LEFT);
		stackpane.prefHeightProperty().bind(scene.heightProperty());
		stackpane.prefWidthProperty().bind((scene.widthProperty().divide(3)));
		treeView = new TreeViewFX(this, modeState);
		treeViewManager = new TreeViewFX(this, !modeState);;
		toolbarUser = new ToolBarFX(this, modeState);
		toolbarManager = new ToolBarFX(this, !modeState);
		tools = new VBox();
		statisticPane = new StackPane();
		statisticPane.setAlignment(Pos.TOP_LEFT);
		statisticPane.prefHeightProperty().bind(scene.heightProperty());
		statisticPane.prefWidthProperty().bind((scene.widthProperty().multiply(2).divide(3)));
		tableView = new StatPane(this);
		menuBar = new MenuFX(this);
		
	}
	
	private void init(Stage primaryStage) throws FileNotFoundException {
		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.setResizable(false);
		primaryStage.setX((SRC_WIDTH - WIN_WIDTH) / 2);
		primaryStage.setY((SRC_HEIGTH - WIN_HEIGTH)/2);
		primaryStage.setScene(scene);
	
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setHgap(1);
		pane.setVgap(1);
        pane.add(menuBar, 0, 0, 2, 1);
        pane.add(setToolBar(modeState), 0, 1, 2, 1);
        pane.add(setTreeViewPane(modeState), 0, 2, 1, 2);
        pane.add(setStatisticPane(), 1, 2, 1, 2);
        
	}
	
	/**
	 * Set defaults and content for the user/manager mode of toolBar pane
	 */
	public Node setToolBar(boolean mode){
		tools.getChildren().clear();
		modeState = mode;
		tools.getChildren().add(modeState ? toolbarManager:toolbarUser);
		return tools; 
	}

	/**
	 * Set defaults and content for the tree view pane
	 */
	public Node setTreeViewPane(boolean mode){
		stackpane.getChildren().clear();
		stackpane.getChildren().add(modeState? treeViewManager:treeView);
		treeView.getSelectionModel().getSelectedItem();
		return stackpane;
	}
	
	public TreeViewFX getTreeView() {
		return modeState? treeViewManager:treeView;
	}
	
	/**
	 * Set defaults and content for the user statistic pane
	 */
	public Node setStatisticPane(){
		Label label = new Label(LABEL_DAILY_STAT);
		statisticPane.getChildren().addAll(label, tableView);
		return statisticPane;
	}
	
	@Override
	public void setStatisticPaneElement(TreeItem newVal) {
		tableView.addElement(newVal);
	}
	    
	@Override 
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
	}

	public static void main(String[] args) { 
		launch(args); 
	}

	@Override
	public void WidgetChanged(IViewColleague col, Object changes) {
		if (col.equals(treeView)||col.equals(toolbarUser)||col.equals(toolbarManager)){
			setTreeViewPane(modeState);
		}
		else if (col.equals(menuBar)){
			this.modeState = menuBar.isModeState();
			setToolBar(modeState);
			setTreeViewPane(modeState);
		}
	}
}

	    
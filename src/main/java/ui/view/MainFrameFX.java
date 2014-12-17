package ui.view;

	/**
	 * Frame:0 - main window 
	 * @author Shmoylova Kseniya
	 */

import interfaces.IViewColleague;
import interfaces.IViewMediator;

import java.awt.Toolkit;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.control.Login;
import ui.control.MenuFX;
import ui.control.ReportTable;
import ui.control.StatTable;
import ui.control.ToolBarStat;
import ui.control.ToolBarTree;
import ui.control.TreeViewFX;
import commonResources.model.UserStat;

public class MainFrameFX extends Application implements IViewMediator{
	
	private static final String APPLICATION_TITLE = "Task tracker";
	private static final String CURRENT_STAT = "Current statistic";
	private static final String REPORT = "Report";
	private static double SRC_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static double SRC_HEIGTH = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	private static double WIN_WIDTH = 800;
	private static double WIN_HEIGTH = 400;
	private GridPane pane;
	private StackPane treeViewPane;
	TabPane tabPane;
	private Login loginPane;
	private final Scene scene;
	
	private TreeViewFX treeView;
	private TreeViewFX treeViewManager;
	private ToolBarTree toolBarTree;
	private ToolBarStat toolBarStat;
	private MenuFX menuBar;
	private VBox treeTools;
	private VBox statTools;
	private StatTable tableView;
	private ReportTable report;
	private boolean modeState =false;

	public MainFrameFX() {
		pane = new GridPane();
		scene = new Scene(pane, WIN_WIDTH, WIN_HEIGTH, Color.ALICEBLUE);
		treeViewPane = new StackPane();
		tabPane = new TabPane();
		treeView = new TreeViewFX(this, modeState);
		treeViewManager = new TreeViewFX(this, !modeState);;
		toolBarTree = new ToolBarTree(this, modeState);
		toolBarStat = new ToolBarStat(this, modeState);
		treeTools = new VBox();
		statTools = new VBox();
		tableView = new StatTable(this);
		report = new ReportTable(this);
		menuBar = new MenuFX(this);
	}
	
	private void init(Stage primaryStage) throws FileNotFoundException {
		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.setResizable(false);
		primaryStage.setX((SRC_WIDTH - WIN_WIDTH) / 2);
		primaryStage.setY((SRC_HEIGTH - WIN_HEIGTH)/2);
		primaryStage.setScene(scene);
	
		treeViewPane.setAlignment(Pos.TOP_LEFT);
		treeViewPane.prefHeightProperty().bind(scene.heightProperty());
		treeViewPane.prefWidthProperty().bind((scene.widthProperty().divide(3)));
		
		tabPane.prefHeightProperty().bind(scene.heightProperty());
		tabPane.prefWidthProperty().bind((scene.widthProperty().multiply(2).divide(3)));
        tabPane.setSide(Side.RIGHT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setHgap(1);
		pane.setVgap(1);
        pane.add(menuBar, 0, 0, 2, 1);
        pane.add(setToolBar(treeTools, toolBarTree), 0, 1, 1, 1);
        pane.add(setTreeViewPane(modeState), 0, 2, 1, 1);
        pane.add(setToolBar(statTools, toolBarStat), 1, 1, 1, 1);
        pane.add(setStatisticPane(), 1, 2, 1, 2);
	}
	
	/**
	 * Set defaults and content for the user/manager mode of toolBar pane
	 */
	public Node setToolBar(VBox box, ToolBar bar){
		box.getChildren().clear();
			box.getChildren().add(bar);
		return box; 
	}

	/**
	 * Set defaults and content for the tree view pane
	 */
	public Node setTreeViewPane(boolean mode){
		treeViewPane.getChildren().clear();
		treeViewPane.getChildren().add(modeState? treeViewManager:treeView);
		treeView.getSelectionModel().getSelectedItem();
		return treeViewPane;
	}
	
	public TreeViewFX getTreeView() {
		return modeState? treeViewManager:treeView;
	}
	
	/**
	 * Set defaults and content for the user current statistic pane
	 */
	public Node setStatisticPane(){
		final Tab tab1 = new Tab();
        tab1.setText(CURRENT_STAT);
        tab1.setContent(tableView);
        
        tabPane.getTabs().addAll(tab1);
		return tabPane;
	}
	
	@Override
	public void setStatisticPaneElement(TreeItem newVal) {
		tableView.addElement(newVal);
	}
	    
	@Override 
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
		login();
		getWindow().setOnCloseRequest(e -> menuBar.disconnect(getUserName(), getStatistic()));
		getWindow().setOnHiding(e -> menuBar.disconnect(getUserName(), getStatistic()));
	}

	public static void main(String[] args) { 
		launch(args); 
	}

	@Override
	public void WidgetChanged(IViewColleague col, Object changes) {
		if (col.equals(treeView)||col.equals(toolBarTree)){
			setTreeViewPane(modeState);
		}
		else if (col.equals(menuBar)){
			this.modeState = menuBar.isModeState();
			toolBarTree.setModeState(modeState);
			toolBarStat.setModeState(modeState);
			setTreeViewPane(modeState);
		}
		else if (col.equals(report)){
			Tab tab = new Tab();
	        tab.setText(REPORT);
	        tab.setContent((Node) changes);
			tabPane.getTabs().add(tab);
		}
	}

	@Override
	public String getUserName() {
		return loginPane.getUserName();
	}

	@Override
	public void login() {
		loginPane = new Login();
	}

	@Override
	public Window getWindow() {
		return scene.getWindow();
	}

	@Override
	public UserStat getStatistic() {
		return tableView.getStatistic();
	}
}
	    
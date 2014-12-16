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
import ui.control.StatPane;
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
	private StackPane statisticPane;
	private Login loginPane;
	private final Scene scene;
	
	private TreeViewFX treeView;
	private TreeViewFX treeViewManager;
	private ToolBarTree toolbarUser;
	private ToolBarTree toolbarManager;
	private ToolBarStat toolbarStatistic;
	private MenuFX menuBar;
	private VBox treeTools;
	private VBox statTools;
	private StatPane tableView;
	private StatPane report;
	private boolean modeState =false;

	public MainFrameFX() {
		pane = new GridPane();
		scene = new Scene(pane, WIN_WIDTH, WIN_HEIGTH, Color.ALICEBLUE);
		treeViewPane = new StackPane();
		statisticPane = new StackPane();
		treeView = new TreeViewFX(this, modeState);
		treeViewManager = new TreeViewFX(this, !modeState);;
		toolbarUser = new ToolBarTree(this, modeState);
		toolbarManager = new ToolBarTree(this, !modeState);
		toolbarStatistic = new ToolBarStat(this, modeState);
		treeTools = new VBox();
		statTools = new VBox();
		tableView = new StatPane(this);
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
		
		statisticPane.setAlignment(Pos.TOP_LEFT);
		statisticPane.prefHeightProperty().bind(scene.heightProperty());
		statisticPane.prefWidthProperty().bind((scene.widthProperty().multiply(2).divide(3)));
		
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setHgap(1);
		pane.setVgap(1);
        pane.add(menuBar, 0, 0, 2, 1);
        pane.add(setToolBar(treeTools, toolbarUser, modeState), 0, 1, 1, 1);
        pane.add(setTreeViewPane(modeState), 0, 2, 1, 1);
        pane.add(setToolBar(statTools, toolbarStatistic, modeState), 1, 1, 1, 1);
        pane.add(setStatisticPane(), 1, 2, 1, 1);
	}
	
	/**
	 * Set defaults and content for the user/manager mode of toolBar pane
	 */
	public Node setToolBar(VBox box, ToolBar toolbar, boolean mode){
		box.getChildren().clear();
		modeState = mode;
		if(toolbar.getClass().equals(ToolBarTree.class))
			box.getChildren().add(modeState ? toolbarManager:toolbarUser);
		else
			box.getChildren().add(toolbarStatistic);
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
	 * Set defaults and content for the user statistic pane
	 */
	public Node setStatisticPane(){
		statisticPane.getChildren().clear();
		statisticPane.getChildren().add(tableView);
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
		login();
		getWindow().setOnCloseRequest(e -> menuBar.disconnect(getUserName(), getStatistic()));
		getWindow().setOnHiding(e -> menuBar.disconnect(getUserName(), getStatistic()));
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
			setToolBar(treeTools, toolbarUser, modeState);
			setToolBar(statTools, toolbarStatistic, modeState);
			setTreeViewPane(modeState);
		}
		else if (col.equals(report)){
			pane.add(statisticPane, 1, 2, 1, 2);
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
	
	//TODO maybe display tab panel when loading
	@Override
	public void createTabPane() {
		TabPane tabPane = new TabPane();
        tabPane.setSide(Side.RIGHT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        final Tab tab1 = new Tab();
        tab1.setText(CURRENT_STAT);
        tab1.setContent(statisticPane);
        final Tab tab2 = new Tab();
        tab2.setText(REPORT);
        report = new StatPane();
        tab2.setContent(report.showReport(getUserName()));
        tabPane.getTabs().addAll(tab1, tab2);
        pane.add(tabPane, 1, 2, 1, 2);
	}
}
	    
package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.mediator.ControllerViewMediator;

import commonResources.model.UserStat;

public class MenuFX extends MenuBar implements IViewColleague{
	
	private static final String MENU_HELP = "Help";
	private static final String MENU_MODE = "Mode";
	private static final String MENU_FILE = "File";
	private static final String SUBMENU_ABOUT = "About ActivityType tracker";
	private static final String SUBMENU_CONTENT = "Help contents";
	private static final String SUBMENU_USER = "User";
	private static final String SUBMENU_MANAGER = "Manager";
	private static final String SUBMENU_EXIT = "Exit";
	private IViewMediator mainFrame;
	private ControllerViewMediator em;
	private boolean modeState;
	private Timeline timeline;
	
	public MenuFX(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		em = ControllerViewMediator.getInstance();
		initMenu();
		treeViewSync();
	}
	
	private void treeViewSync() {
		timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        EventHandler<ActionEvent> onTimer = (event -> em.loadActivityTypes());
        Duration duration = Duration.minutes(5);
        KeyFrame keyFrame = new KeyFrame(duration, onTimer);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
	}
	
	public MenuItem createMenuItem(String title, EventHandler<ActionEvent> event){
		MenuItem newItem = new MenuItem();
		newItem.setText(title);
		newItem.setOnAction(event);
		return newItem;	
	}
	
	public Menu createMenu(String title, MenuItem ... items){
		Menu newMenu = new Menu();
		newMenu.setText(title);
		newMenu.getItems().addAll(items);
		return newMenu;	
	}

	public void initMenu(){    
		MenuItem menu101 = createMenuItem(SUBMENU_EXIT, event -> systemExit());
		MenuItem menu201 = createMenuItem(SUBMENU_MANAGER, event -> setModeState(true)); 
        MenuItem menu202 = createMenuItem(SUBMENU_USER, event -> setModeState(false));   
        MenuItem menu301 = createMenuItem(SUBMENU_CONTENT, event -> getHelp());
        menu301.setAccelerator(KeyCombination.keyCombination("F1"));  
        MenuItem menu302 = createMenuItem(SUBMENU_ABOUT,event -> getDescription() );
        
        Menu menu1 = createMenu(MENU_FILE, menu101);
        Menu menu2 = createMenu(MENU_MODE, menu201, menu202);
        Menu menu3 = createMenu(MENU_HELP, menu301, menu302);

        this.getMenus().addAll(menu1,menu2,menu3);
	}
	
	private void systemExit() {
		((Stage) mainFrame.getWindow()).close();
	}
	public void disconnect(String userName, UserStat statistic) {
		em.disconnect(userName, statistic);
	}

	private void getDescription() {
		// TODO Auto-generated method stub
	}

	private void getHelp() {
		// TODO Auto-generated method stub
	}
	
	public boolean isModeState() {
		return modeState;
	}

	public void setModeState(boolean modeState) {
		this.modeState = modeState;
		if(modeState){
			timeline.stop();
		}else
			timeline.play();
		changed(this);
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}
}

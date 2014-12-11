package ui.control;

import interfaces.IViewColleague;
import interfaces.IViewMediator;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.input.KeyCombination;

public class MenuFX extends MenuBar implements IViewColleague{
	private static final String MENU_HELP = "Help";
	private static final String MENU_REPORTS = "Reports";
	private static final String MENU_MODE = "Mode";
	private static final String MENU_FILE = "File";
	private static final String SUBMENU_ABOUT = "About Task tracker";
	private static final String SUBMENU_CONTENT = "Help contents";
	private static final String SUBMENU_EXPORT = "Export";
	private static final String SUBMENU_LOAD_STAT = "Load statistic";
	private static final String SUBMENU_USER = "User";
	private static final String SUBMENU_MANAGER = "Manager";
	private static final String SUBMENU_EXIT = "Exit";
	private static final String SUBMENU_LOAD_REPORT = "Load report";
	private static final String SUPPRESS_DEPRECATION = "deprecation";
	private IViewMediator mainFrame;
	private boolean modeState;
	
	public MenuFX(IViewMediator mainFrame) {
		this.mainFrame = mainFrame;
		initMenu();
	}

	@SuppressWarnings(SUPPRESS_DEPRECATION)
	public void initMenu(){
		MenuItem menu101 = MenuItemBuilder.create().text(SUBMENU_LOAD_REPORT).build();
        menu101.setOnAction(event -> loadReport());        
        MenuItem menu102 = MenuItemBuilder.create().text(SUBMENU_EXIT).build();
        menu102.setOnAction(event -> systemExit()); 
        
		MenuItem menu201 = MenuItemBuilder.create().text(SUBMENU_MANAGER).build();
        menu201.setOnAction(event -> setModeState(true));        
        MenuItem menu202 = MenuItemBuilder.create().text(SUBMENU_USER).build();
        menu202.setOnAction(event -> setModeState(false));        

        MenuItem menu301 = MenuItemBuilder.create().text(SUBMENU_LOAD_STAT).build();
        menu301.setOnAction(event -> loadStatistic());  
        MenuItem menu302 = MenuItemBuilder.create().text(SUBMENU_EXPORT).build();
        menu302.setOnAction(event -> createStatFile());  
        
        
        MenuItem menu401 = MenuItemBuilder.create().text(SUBMENU_CONTENT).accelerator(KeyCombination.keyCombination("F1")).build();
        menu401.setOnAction(event -> getHelp());        
        MenuItem menu402 = MenuItemBuilder.create().text(SUBMENU_ABOUT).build();
        menu402.setOnAction(event -> getDescription()); 
        
        // Options menu
        Menu menu1 = new Menu();
        menu1.setText(MENU_FILE);
        Menu menu2 = new Menu();
        menu2.setText(MENU_MODE);
        menu2.getItems().addAll(menu201,menu202);
        Menu menu3 = new Menu();
        menu3.setText(MENU_REPORTS);
        menu3.getItems().addAll(menu301, menu302);
        Menu menu4 = new Menu();
        menu4.setText(MENU_HELP);
        menu4.getItems().addAll(menu401, menu402);
        this.getMenus().addAll(menu1,menu2,menu3,menu4);
	}
	
	private Object systemExit() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object loadReport() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object createStatFile() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object loadStatistic() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isModeState() {
		return modeState;
	}

	public void setModeState(boolean modeState) {
		this.modeState = modeState;
		changed(this);
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}
}

package ui.control;

import commonResources.model.ActivityType;
import interfaces.IControllerViewMediator;
import interfaces.IViewColleague;
import interfaces.IViewMediator;
import interfaces.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import ui.mediator.ControllerViewMediator;

@SuppressWarnings({ "unchecked", "rawtypes" })

public class TreeViewFX extends TreeView implements IViewColleague, Observer{
	
	private static final String NEW_ACTIVITY_TYPE = "New activityType";
	private IViewMediator mainFrame;
	private IControllerViewMediator em;
	private TreeItem<ActivityType> treeRoot;
	private boolean modeState;

	public TreeViewFX(IViewMediator mainFrame, boolean mode) {
		this.mainFrame = mainFrame;
		this.modeState = mode;
		em = ControllerViewMediator.getInstance();
		em.register(this);
		if(!modeState)
			initUserMode();
		else
			initManagerMode();
	}
	
	/**
	 * An implementation of the TreeView control displaying an expandable tree pane
	 * node.
	 */
	
	public Node initUserMode(){
		this.setShowRoot(false);
		this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ActivityType>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<ActivityType>> observable,
					TreeItem<ActivityType> oldVal, TreeItem<ActivityType> newVal) {
				// TODO inform the controller about the selected activityType changes
				try{
					if(oldVal!=null && !newVal.getValue().getOwner().equalsIgnoreCase(mainFrame.getUserName())){
						getSelectionModel().clearAndSelect(getRow(oldVal));
					}
					else
						mainFrame.setStatisticPaneElement(newVal);
				}catch (NullPointerException |IndexOutOfBoundsException e){
				}
				
			}
		});
		return this;
	}
	
	public Node initManagerMode(){
		this.setShowRoot(false);
		this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ActivityType>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<ActivityType>> observable,
					TreeItem<ActivityType> oldVal, TreeItem<ActivityType> newVal) {
						mainFrame.setStatisticPaneElement(newVal);
						// TODO inform the controller about the selected activityType changes
			}
		});
		
		this.setEditable(true);
		this.setCellFactory(new Callback<TreeView<ActivityType>,TreeCell<ActivityType>>(){
			@Override
			public TreeCell<ActivityType> call(TreeView<ActivityType> p) {
				return new TextFieldTreeCellImpl();
			}
		});
		return this;
	}
	
	private final class TextFieldTreeCellImpl extends TreeCell<ActivityType> {

		private TextField textField;
		private ContextMenu addMenu = new ContextMenu();
		 
		public TextFieldTreeCellImpl() {
			MenuItem addMenuItem = new MenuItem(NEW_ACTIVITY_TYPE);
			addMenu.getItems().add(addMenuItem);
			addMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {
					TreeItem<ActivityType> newActivityType = insertActivityType();
					getTreeItem().getChildren().add(newActivityType);
				}
			});
		}
		 
		@Override
		public void startEdit() {
			super.startEdit();
			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}
		 
		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText(((ActivityType) getItem()).toString());
			setGraphic(getTreeItem().getGraphic());
		}
		 
		@Override
		public void updateItem(ActivityType item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
					setContextMenu(addMenu);
				}
			}
		}
		 
		private void createTextField() {
			textField = new TextField(getString());
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(editActivityType(textField.getText()));
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
		}
		 
		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
	
	private ActivityType editActivityType(String text) {
		// TODO inform the controller about the activityType changes
		ActivityType activityType = ((TreeItem<ActivityType>) this.getSelectionModel().getSelectedItem()).getValue();
		activityType.setActivityTypeTitle(text);
		return activityType;
	}
	
	private TreeItem<ActivityType> insertActivityType() {
		// TODO inform the controller about the activityType changes
		TreeItem<ActivityType> newActivityType = new TreeItem<ActivityType>(new ActivityType(NEW_ACTIVITY_TYPE, 1));
		return newActivityType;
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}

	public void loadActivityTypes() {
		em.loadActivityTypes();
	}

	public void buildTree(ActivityType activityType, TreeItem<ActivityType> taskRoot) {
		TreeItem<ActivityType> subItem = new TreeItem<ActivityType>(activityType);
		taskRoot.getChildren().add(subItem);
		for(ActivityType t:((ActivityType) activityType).getActivityType())
			buildTree(t, subItem);
	}
	
	@Override
	public void update(ActivityType activityType) {
		treeRoot = new TreeItem<ActivityType>((ActivityType) activityType);
		for(ActivityType t:((ActivityType) activityType).getActivityType())
			buildTree(t, treeRoot);

		this.setRoot(treeRoot);
		treeRoot.setExpanded(true);
		changed(this);
	}
}

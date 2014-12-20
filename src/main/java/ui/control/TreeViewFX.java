package ui.control;

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
import commonResources.model.ActivityType;

@SuppressWarnings({ "unchecked", "rawtypes" })

public class TreeViewFX extends TreeView implements IViewColleague, Observer{
	
	private static final String NEW_ACTIVITY_TYPE = "Create";
	private static final String REMOVE_ACTIVITY_TYPE = "Delete";
	private static final String EMPTY_STRING = "";
	private IViewMediator mainFrame;
	private IControllerViewMediator em;
	private TreeItem<ActivityType> treeRoot;
	private boolean modeState;
	private TreeItem<ActivityType> selected;
	boolean isAvailable=false;

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
					if(!(newVal.getValue().getOwner().equalsIgnoreCase(mainFrame.getUserName())||newVal.getValue().getOwner().equalsIgnoreCase("shared"))){
						getSelectionModel().clearAndSelect(getRow(oldVal));
					}else{
						mainFrame.setStatisticPaneElement(newVal);
						selected = newVal;
					}
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
						mainFrame.setStatisticPaneElement(treeRoot);
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
		private TreeItem<ActivityType> newActivityType;
		 
		public TextFieldTreeCellImpl() {
			setMenuCreate();
			setMenuRemove();
		}

		private void setMenuCreate() {
			MenuItem addMenuItem = new MenuItem(NEW_ACTIVITY_TYPE);
			addMenu.getItems().add(addMenuItem);
			addMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {
					try{
						insertItem(getTreeItem());
					}catch(NullPointerException e){
						insertItem(treeRoot);
					}
				}

				private void insertItem(TreeItem<ActivityType> treeItem) {
					newActivityType = insertActivityType(treeItem.getValue().getActivityTypeID());
					treeItem.getChildren().add(newActivityType);
				}
			});
		}
		
		private void setMenuRemove() {
			MenuItem removeMenuItem = new MenuItem(REMOVE_ACTIVITY_TYPE);
			addMenu.getItems().add(removeMenuItem);
			removeMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {
					getTreeItem().getParent().getChildren().remove(getTreeItem());
					em.removeActivityTypeElement(getTreeItem().getValue());
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
			return getItem() == null ? EMPTY_STRING : getItem().toString();
		}
	}
	
	private ActivityType editActivityType(String text) {
		ActivityType activityType = ((TreeItem<ActivityType>) this.getSelectionModel().getSelectedItem()).getValue();
		activityType.setActivityTypeTitle(text);
		return activityType;
	}
	
	private TreeItem<ActivityType> insertActivityType(int parentID) {
		TreeItem<ActivityType> newActivityType = new TreeItem<ActivityType>(em.insertActivityTypeElement(parentID));
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

	public boolean checkSelected(){
		return selected != null ? true:false;
	}

	public boolean checkAvailable(TreeItem<ActivityType> treeItem){
		if(treeItem.getValue().getActivityTypeID() == selected.getValue().getActivityTypeID())
			isAvailable = true;
		for (TreeItem<ActivityType> tr:treeItem.getChildren() )
			checkAvailable(tr);
		return isAvailable;
	}
	
	@Override
	public void update(Object object) {
		if(object.getClass() == ActivityType.class){
			treeRoot = new TreeItem<ActivityType>((ActivityType) object);
			for(ActivityType t:((ActivityType) object).getActivityType())
				buildTree(t, treeRoot);

			this.setRoot(treeRoot);
			treeRoot.setExpanded(true);
			if(checkSelected()){
				if(checkAvailable(treeRoot)){
					this.getSelectionModel().select(selected);
					isAvailable =false;
				}
				else
					;//Alert message: The chosen task is no longer relevant
			}
			changed(this);
		}
	}
}

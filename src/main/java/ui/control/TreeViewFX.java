package ui.control;

import commonResources.model.Task;
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
	
	private static final String NEW_TASK = "New task";
	private IViewMediator mainFrame;
	private IControllerViewMediator em;
	private TreeItem<Task> treeRoot;
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
		this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Task>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Task>> observable,
					TreeItem<Task> oldVal, TreeItem<Task> newVal) {
				// TODO inform the controller about the selected task changes
				try{
				if(oldVal!=null && !newVal.getValue().getOwner().equalsIgnoreCase("Me")){
					mainFrame.getTreeView().getSelectionModel().clearAndSelect(getRow(oldVal));
					newVal=oldVal;
				}
					mainFrame.setStatisticPaneElement(newVal);
				
				}catch (NullPointerException |IndexOutOfBoundsException e){
				}
				
			}
		});
		return this;
	}
	
	public Node initManagerMode(){
		this.setShowRoot(false);
		this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Task>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Task>> observable,
					TreeItem<Task> oldVal, TreeItem<Task> newVal) {
						mainFrame.setStatisticPaneElement(newVal);
						// TODO inform the controller about the selected task changes
			}
		});
		
		this.setEditable(true);
		this.setCellFactory(new Callback<TreeView<Task>,TreeCell<Task>>(){
			@Override
			public TreeCell<Task> call(TreeView<Task> p) {
				return new TextFieldTreeCellImpl();
			}
		});
		return this;
	}
	
	private final class TextFieldTreeCellImpl extends TreeCell<Task> {

		private TextField textField;
		private ContextMenu addMenu = new ContextMenu();
		 
		public TextFieldTreeCellImpl() {
			MenuItem addMenuItem = new MenuItem(NEW_TASK);
			addMenu.getItems().add(addMenuItem);
			addMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {
					TreeItem<Task> newTask = insertTask();
					getTreeItem().getChildren().add(newTask);
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
			setText(((Task) getItem()).toString());
			setGraphic(getTreeItem().getGraphic());
		}
		 
		@Override
		public void updateItem(Task item, boolean empty) {
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
						commitEdit(editTask(textField.getText()));
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
	
	private Task editTask(String text) {
		// TODO inform the controller about the task changes
		Task task = ((TreeItem<Task>) this.getSelectionModel().getSelectedItem()).getValue();
		task.setTaskTitle(text);
		return task;
	}
	
	private TreeItem<Task> insertTask() {
		// TODO inform the controller about the task changes
		TreeItem<Task> newTask = new TreeItem<Task>(new Task(NEW_TASK, 1));
		return newTask;
	}

	@Override
	public void changed(Object changes) {
		mainFrame.WidgetChanged(this, changes);
	}

	public void loadTasks() {
		em.loadTasks();
	}

	public void buildTree(Task task, TreeItem<Task> taskRoot) {
		TreeItem<Task> subItem = new TreeItem<Task>(task);
		taskRoot.getChildren().add(subItem);
		for(Task t:((Task) task).getTask())
			buildTree(t, subItem);
	}
	
	@Override
	public void update(Task task) {
		treeRoot = new TreeItem<Task>((Task) task);
		for(Task t:((Task) task).getTask())
			buildTree(t, treeRoot);

		this.setRoot(treeRoot);
		treeRoot.setExpanded(true);
		changed(this);
	}
}

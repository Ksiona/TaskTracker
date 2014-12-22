package ui.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.mediator.ControllerViewMediator;

public class Login extends Stage{
	private static final String TITLE = "Welcome";
	private static final String FONT = "Tahoma";
	private static final String LABEL_USER_NAME = "User Name:";
	private static final String BUTTON_SIGN_IN = "Sign in";
	private GridPane loginPane;
	private Scene scene;
	private ControllerViewMediator em;
	private String userName;

	public Login() {
		em = ControllerViewMediator.getInstance();
		loginPane = new GridPane();
		this.initStyle(StageStyle.TRANSPARENT);
		this.setAlwaysOnTop(true);
		scene = new Scene(loginPane, 300, 100, Color.ALICEBLUE);

		this.setScene(scene);
		this.centerOnScreen();
		init();
		this.showAndWait();
	}
	
	public void init(){
		Text sceneTitle = new Text(TITLE);
		sceneTitle.setFont(Font.font(FONT, FontWeight.NORMAL, 16));
		sceneTitle.setTextAlignment(null);
		
		loginPane.setAlignment(Pos.CENTER);
		loginPane.setHgap(10);
		loginPane.setVgap(10);
		loginPane.add(sceneTitle, 0, 0, 3, 1);

        Label name = new Label(LABEL_USER_NAME);
        loginPane.add(name, 0, 1);
        TextField userTextField = new TextField();
        loginPane.add(userTextField, 2, 1);

        Button btn = new Button(BUTTON_SIGN_IN);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        loginPane.add(hbBtn, 2, 2);

        btn.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER), 
      		  new Runnable() {
      		    @Override public void run() {
      		    	btn.fire();
      		    }
      		  }
      		);
        btn.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		userName = userTextField.getText();
        		if(em.login(userName))
        			close();
        		else
        			userTextField.setText("Reenter your name");
        	}
        });
	}

	public String getUserName() {
		return userName;
	}
}

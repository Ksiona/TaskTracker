package init;

import java.io.IOException;

import client.NetClient;
import logic.ActivityTypeProcessor;
import server.ServerThread;
import ui.mediator.ControllerViewMediator;
import ui.view.MainFrameFX;

public class TaskTracker {

	private static ServerThread srv;
	
	public TaskTracker() {
        // Start server
        try {
			srv = new ServerThread();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        new Thread(srv).start();
     // Start client
		initApp();
	}
	
	public void initApp(){
		NetClient.getInstance();
		ActivityTypeProcessor.getInstance();
		ControllerViewMediator.getInstance();

		MainFrameFX.main(new String[]{});
	}
	
	public static void main(String[] args) {
		new TaskTracker();
	}

}

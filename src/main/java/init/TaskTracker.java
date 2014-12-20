package init;

import java.io.IOException;

import org.apache.log4j.Logger;

import server.ServerThread;
import ui.mediator.ControllerViewMediator;
import ui.view.MainFrameFX;

public class TaskTracker {

	private static final Logger log = Logger.getLogger(TaskTracker.class);
	private static ServerThread srv;
	
	
	public TaskTracker() {
        // Start server
        try {
			srv = new ServerThread();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
        new Thread(srv).start();
     // Start client
		initApp();
	}
	
	public void initApp(){
		ControllerViewMediator.getInstance();
		MainFrameFX.main(new String[]{});
	}
	
	public static void main(String[] args) {
		new TaskTracker();
	}
}

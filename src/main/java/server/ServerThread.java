package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import commonResources.model.TrackerUser;

public class ServerThread implements Runnable{
	private static final String STATUS_STOPED = "Server stoped";
	private static final String FILE_PATH = "./target/classes/storage/userlist.txt";
	private final HashMap<String, Method> methods = new HashMap<String, Method>(); // кэш методов
    private ServerSocket server;
    private Socket socket;
    protected Collection<TrackerUser> userList;
    private ExecutorService scheduledThreadPool = Executors.newFixedThreadPool(4);
    private static final Logger log = Logger.getLogger(ServerThread.class);
    private InputStreamReader inputStream;

    public ServerThread() throws IOException {
    	server = new ServerSocket(6284);
    	inputStream = new InputStreamReader(new FileInputStream(new File(FILE_PATH)),"UTF-8");
    	loadUserList(); 
	}
    
    private void loadUserList() {
    	userList =  new HashSet<TrackerUser>();
    	try (Scanner scanner = new Scanner(inputStream)){
			while (scanner.hasNext()) {
				String userName = scanner.next();
				userList.add(new TrackerUser(userName, 0));
				log.info("Stream Read" + userList.size());
			}
		}
	}

	@Override
    public void run() {
    	final Class<?> clazz = VariableEssence.class;
    	for (Method m : clazz.getMethods()) 
    		methods.put(m.getName(), m);
        try {
           while(true){
        	socket = server.accept();
            WorkerThread worker = new WorkerThread(socket, methods, userList);
            scheduledThreadPool.execute(worker);
           }
        }catch(Exception e){
        	log.error(e.getMessage(), e);
        }
    }
    
    public void shutdownServer(){
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
        scheduledThreadPool.shutdown();
        while(!scheduledThreadPool.isTerminated()){
        }
       log.info(STATUS_STOPED);
    }
}
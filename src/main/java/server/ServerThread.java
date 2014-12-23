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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import commonResources.model.TrackerUser;

/**
 * @author Shmoylova Kseniya
 * Class is responsible for initialization of server socket, 
 * listening port and invocation threads processing data
 *
 */
public class ServerThread implements Runnable{
	
    private static final Logger log = Logger.getLogger(ServerThread.class);
	private static final String STATUS_STOPED = "Server stoped";
	private static final String FILE_PATH = "./target/classes/storage/userlist.txt";
	private static final String CHARSET = "UTF-8";
	private final HashMap<String, Method> methods = new HashMap<String, Method>();
    private ServerSocket server;
    private Socket socket;
    protected Collection<TrackerUser> userList;
    private ExecutorService threadPool;
	private ConcurrentMap<String, WorkerThread> peers;

    private InputStreamReader inputStream;

    public ServerThread() throws IOException {
    	server = new ServerSocket(6284);
    	peers = new ConcurrentHashMap<String, WorkerThread>(4);
    	threadPool = Executors.newFixedThreadPool(4);
    	inputStream = new InputStreamReader(new FileInputStream(new File(FILE_PATH)),CHARSET);
    	loadUserList(); 
	}
    
    /**
     * Method fills the {@link ServerThread#userList} from a file resource
     * @resource userlist.txt
     */
    private void loadUserList() {
    	userList =  new HashSet<TrackerUser>();
    	try (Scanner scanner = new Scanner(inputStream)){
			while (scanner.hasNext()) {
				String userName = scanner.next();
				userList.add(new TrackerUser(userName));
			}
		}
	}

    /**
     * The main loop waiting for a connection and processing of incoming calls
     * Fills the methods cache of class VariableEssence
     * Listens to the port, if it receive a call, create an instance WorkerThread, which pass a reference to itself, the incoming call and cache methods.
     * launch an instance is created using an ExecutorService, adding it to the pool
     * @see WorkerThread
     * @exception Exception catch all that is not processed inside, stop the server
    */
	@Override
    public void run() {
    	final Class<?> clazz = VariableEssence.class;
    	for (Method m : clazz.getMethods()) 
    		methods.put(m.getName(), m);
        try {
        	while(true){
        		socket = server.accept();
        		WorkerThread worker = new WorkerThread(socket, methods, this);
        		threadPool.execute(worker);
        	}
        }catch(Exception e){
        	log.error(e.getMessage(), e);
        	shutdownServer();
        }
	}
	
	/**
	 * Method is called from an instance WorkerThread, during connection, when it is known the name of the user.
	 * This method checks whether there is no user with this name in the list, if not - register it.
	 * @param worker WorkerThread trying to register
	 * @return If the name is not duplicated is true, otherwise - false
	 */
	public boolean tryRegister(WorkerThread worker, String userName) {
		return peers.putIfAbsent(userName, worker) == null;
	}
	
	/**
	 * Method is called from an instance WorkerThread when the user disconnects.
	 * The method removes a user registration in the list of connected users.
	 * @param wt - WorkerThread who wants to unregister
	 * @return - true - successful logout, false - list not contains.
	 */
	boolean unregister(WorkerThread wt, String userName) {
		return peers.remove(userName) == wt;
	}
	
	/** 	
	 * Checks if this WorkerThread in the connected list
	 * @param wt - checked WorkerThread
	 * @return true -it is, false - not.
	 */
	boolean isRegistered(WorkerThread wt) {
		return peers.containsValue(wt);
	}  
    
	/**
	 * Shutdown the server
	 * Given 3 seconds to possible pending WorkerThread
	 * Try to terminate pool
	 * If the server socket is not closed, trying to close it
	 * Wait until the ExecutorService will not throw the flag isTerminated
	 * Interrupts ServerThread
	 */
	public void shutdownServer(){
		try {
			Thread.sleep(3000);
			threadPool.shutdown();
			if (server != null && !server.isClosed())
				server.close();
		} catch (IOException | InterruptedException e) {
			log.error(e.getMessage(), e);;
		}
		while(!threadPool.isTerminated()){
		}
		log.info(STATUS_STOPED);
		Thread.currentThread().interrupt();
	}
}
package server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import commonResources.model.TrackerUser;

public class ServerThread implements Runnable{
	private static final String STATUS_STOPED = "Server stoped";
	private final HashMap<String, Method> methods = new HashMap<String, Method>(); // кэш методов
    private ServerSocket server;
    private Socket socket;
    public Collection<TrackerUser> userList;
    Lock lock = new ReentrantLock();
    private ExecutorService scheduledThreadPool = Executors.newFixedThreadPool(4);

    public ServerThread() throws IOException {
    	server = new ServerSocket(6284);
    	userList = new HashSet<TrackerUser>();
	}
    
    @Override
    public void run() {
    	final Class<?> clazz = VariableEssence.class;
    	for (Method m : clazz.getMethods()) 
    		methods.put(m.getName(), m);
        try {
           while(true){
        	socket = server.accept();
            WorkerThread worker = new WorkerThread(socket, methods, this);
            scheduledThreadPool.execute(worker);
           }
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    public void shutdownServer(){
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        scheduledThreadPool.shutdown();
        while(!scheduledThreadPool.isTerminated()){
        }
        System.out.println(STATUS_STOPED);
    }
}
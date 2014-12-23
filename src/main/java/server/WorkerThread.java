package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import commonResources.model.TrackerUser;

/**
 * @author Shmoylova Kseniya
 * Define runnable object, processing incoming calls
 */
public class WorkerThread implements Runnable{
 
	private static final Logger log = Logger.getLogger(WorkerThread.class);
	private static final String USER_CONNECTED = "Подключен пользователь: ";
	private static final String USER_DISCONNECTED = "Пользователь отключился: ";
	private Socket socket;
	private String userName;
	private final HashMap<String, Method> methods;
	private DataInputStream is;
	private DataOutputStream ous;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ServerThread server;
	private VariableEssence obj; 
	private boolean isConnected;
	private Lock lock = new ReentrantLock();
	private boolean isAccepted;
     
	/**
	 * Instantiate {@link VariableEssence} object whose methods will be called
	 * Defines in and out streams associated with socket
	 * @param socket
	 * @param methods
	 * @param server
	 */
    public WorkerThread(Socket socket, HashMap<String, Method> methods, ServerThread server){
        this.socket=socket;
        this.methods= methods;
        this.server =server;
        obj = new VariableEssence(this);
	   	try {
			is = new DataInputStream(socket.getInputStream());
			ous = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    }

    /**
     * The main loop processing incoming calls while user is connected
     * Reads method name from stream, then checks arguments quantity
     * If method is need arguments, fills args vector from ObjectInputStream
     * Result of method invocation writing by ObjectOutputStream
     * @exception IOException - stream errors
     * @exception IllegalAccessException | IllegalArgumentException| InvocationTargetException | ClassNotFoundException - reflect invocation errors
     * On exception logFile and invoke {@link WorkerThread#onUserDisconnected(String)}
     */
	@Override
    public void run() {
		try {
		   	isConnected = true;
	        while (isConnected) {
	            String methodName = is.readUTF();
	            Method method = methods.get(methodName);
	            if (method == null) {
	            } else {
	            	final Vector<Object> args = new Vector<Object>();
	            	if (method.getParameterTypes().length > 0) {
	            		ois = new ObjectInputStream(is);
	            		for (int i = 0; i < method.getParameterTypes().length; i++) {
	            			Object o = ois.readObject();
	            			args.add(o);
	            		}
	            	}
	            	Object result = method.invoke(obj, args.toArray());
	            	if (result!=null){
	            		oos = new ObjectOutputStream(ous);
	            		oos.writeObject(result);
	            		oos.flush();
	            	}
	            }
	        }
        } catch (IOException 
        		| IllegalAccessException 
        		| IllegalArgumentException 
        		| InvocationTargetException 
        		| ClassNotFoundException e) {
        	onUserDisconnected(userName);
        	log.error(e.getMessage(), e);
		}
    }
	
	/**
	 * User connection
	 * Synchronized access to the user list
	 * Method checks the passed name in the user list
	 * causes the user is added to the list of connected if the validation - success
	 * @param userName is the name passed by the client
	 * @return true if the user is in the list of server and registered in the list of connected, false otherwise
	 */
    public boolean onUserConnection(String userName) {
    	isAccepted = false;
    	lock.lock();
    	try { 
			for(TrackerUser user:server.userList){
				if(user.getUserName().equalsIgnoreCase(userName)){
					isAccepted = server.tryRegister(this, userName);
					this.userName = userName;
					log.info(USER_CONNECTED+ userName);
					break;
				}
			}
		}finally {
			lock.unlock(); 
		}
		return isAccepted;
    }
	 
    /**
     * User disconnection
     * Method deletes a user from the list of connected and closes the socket
     * @exception IOException if a write error in the socket, the logfile
     */
    public void onUserDisconnected(String userName) {
		isConnected = false;
		log.info(USER_DISCONNECTED + userName);
		server.unregister(this, userName);
		try {
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}		
	}
}

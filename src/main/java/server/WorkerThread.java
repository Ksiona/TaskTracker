package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import commonResources.model.TrackerUser;

public class WorkerThread implements Runnable{
 
private static final String USER_CONNECTED = "Подключен пользователь: ";
private static final String USER_DISCONNECTED = "Пользователь отключился: ";
private Socket socket;
private final HashMap<String, Method> methods;
private DataInputStream is;
private DataOutputStream ous;
protected Collection<TrackerUser> userList;
private VariableEssence obj; 
private boolean isConnected;
private Lock lock = new ReentrantLock();
private boolean isAccepted;
private static final Logger log = Logger.getLogger(WorkerThread.class);
     
    public WorkerThread(Socket socket, HashMap<String, Method> methods, Collection<TrackerUser> userList){
        this.socket=socket;
        this.methods= methods;
        this.userList =userList;
        obj = new VariableEssence(this); // физический объект, чьи методы будем вызывать
    }

	@Override
    public void run() {
		try {
			log.info("");
			is = new DataInputStream(socket.getInputStream());
		   	ous = new DataOutputStream(socket.getOutputStream());
		   	isConnected = true;
	        while (isConnected) {
	            String methodName = is.readUTF();
	            Method method = methods.get(methodName);
	            if (method == null) {
	            } else {
	            	final Vector<Object> args = new Vector<Object>();
	            	if (method.getParameterTypes().length > 0) {
	            		ObjectInputStream ois = new ObjectInputStream(is);
	            		for (int i = 0; i < method.getParameterTypes().length; i++) {
	            			Object o = ois.readObject();
	            			args.add(o);
	            		}
	            	}
	            	Object result = method.invoke(obj, args.toArray());
	            	if (result!=null){
	            		ObjectOutputStream oos = new ObjectOutputStream(ous);
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
        	log.error(e.getMessage(), e);
		}
    }

    public boolean onUserConnection(String userName) {
    	isAccepted = false;
    	lock.lock();
    	try { 
			for(TrackerUser user:userList){
				if(user.getUserName().equalsIgnoreCase(userName)){
					isAccepted = true;
					log.info(USER_CONNECTED+ userName);
					break;
				}
			}
		}finally {
			lock.unlock(); 
		}
		return isAccepted;
    }
	 
	public void onUserDisconnected(String userName) {
		try {
			isConnected = false;
			log.info(USER_DISCONNECTED + userName);
			ous.flush();
			//socket.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}		
	}
}

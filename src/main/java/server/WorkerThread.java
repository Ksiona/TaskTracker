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

public class WorkerThread implements Runnable{
 
private Socket socket;
private final HashMap<String, Method> methods;
private DataInputStream is;
private DataOutputStream ous;
ServerThread serverThread;
VariableEssence obj; 
private boolean isConnected;
     
    public WorkerThread(Socket socket, HashMap<String, Method> methods, ServerThread serverThread){
        this.socket=socket;
        this.methods= methods;
        this.serverThread =serverThread;
        obj = new VariableEssence(this); // физический объект, чьи методы будем вызывать
    }

	@Override
    public void run() {
		try {
			System.out.println(Thread.currentThread().getName());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void onUserConnection(String userName, int role) {
    	/*	serverThread.lock.lock();
		try { 
			serverThread.userList.add(new TrackerUser(userName, role));
			System.out.println("\nПодключен новый пользователь: "+userName);
		}finally {
			serverThread.lock.unlock(); 
		  System.out.println("unlock");
		}*/
	// TODO Receive task list
}
	 
	public void onUserDisconnected(String userName) {
		// TODO get statistic
	/*  	lock.lock();
			try {
		System.out.println("\nПользователь отключился: "+ userName);
	
			for(TaskTrackerUser connected : userList) {
				if(connected.getUserName().equalsIgnoreCase(userName))
					userList.remove(connected);
			}	
			}finally {
				  lock.unlock(); 
				  System.out.println("unlock");
				}*/
			try {
				isConnected = false;
				ous.flush();
				//socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
}

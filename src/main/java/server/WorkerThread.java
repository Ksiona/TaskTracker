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
     
    public WorkerThread(Socket socket, HashMap<String, Method> methods, ServerThread server){
        this.socket=socket;
        this.methods= methods;
        this.server =server;
        obj = new VariableEssence(this); // физический объект, чьи методы будем вызывать
	   	try {
			is = new DataInputStream(socket.getInputStream());
			ous = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    }

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
	 * Подключение пользователя
	 * Синхронизирован доступ к списку пользователей
	 * Метод проверяет наличие переданного имени в списке пользователей
	 * вызывает добавление пользователя в список рассылки
	 * @param userName - имя, передаваемое клиентом 
	 * @return true - пользователь есть в списке сервера и зарегистрирован в списке рассылки, false - нет
	 * @exception IOException при ошибке записи в сокет, запись лога
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
	 * Отключение пользователя
	 * Метод вызывает удаление пользователя из списка рассылки и закрывает сокет
	 * @exception IOException при ошибке записи в сокет, запись лога
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

	/**
	 * Послать сообщение, если оно существует
	 * @exception IOException при ошибке записи в сокет, запись лога
	 */
	public void send(Object alert) {
    	if (alert!=null){
    		try {
				oos.writeObject(alert);
	    		oos.flush();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
    	}
	}

	/**
	 * Метод инициирует рассылку сообщения
	 */
	public void sendAlert(Object alert) {
		server.alertSender(this, alert);
	}
}

package client;

import interfaces.INetClient;
import interfaces.Observer;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import commonResources.interfaces.IVariableEssence;
import commonResources.model.Task;
import commonResources.model.TrackerUser;

public class NetClient implements INetClient {
	private Socket socket;
	private IVariableEssence clientElement;
    private String userName = "Me";
    private int role = 0;	
    private List<Observer> observers;
    Task tasks;
    private static final NetClient INSTANCE = new NetClient();

	public NetClient() {
		this.observers=new ArrayList<>();
        // Создаём прокси
        try {
			socket = new Socket("localhost", 6284);
		} catch (IOException e) {
			e.printStackTrace();
		}
        ClientProxy p = new ClientProxy();
        p.socket = socket;

        // Создаём экземпляр прокси-объекта
       clientElement = (IVariableEssence)Proxy.newProxyInstance(ClientProxy.class.getClassLoader(),
                new Class<?>[] {IVariableEssence.class},
                p);
	}
	
	public static NetClient getInstance(){
		return INSTANCE;
	}
    
	public static void print(Task task){
		System.out.println(task);
		for (Task t: task.getTask())
			print(t);
	}
	
    public static void main(String[] args) throws IOException {

	        new NetClient();	
	}

	@Override
	public void loadTasks() {
		tasks = clientElement.getTaskTree();
		notifyObservers();
	}

	@Override
	public void setTaskTree(Task task) {
		clientElement.setTaskTree(task);
		
	}

	@Override
	public void loadStat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(Date date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadStat(TrackerUser user, Date firstDate, Date lastDay) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void login() {
		clientElement.login(userName, role);
	}
	
	@Override
	public void register(Observer obj) {
		if(obj == null) 
			throw new NullPointerException();
		if(!observers.contains(obj)) {
			observers.add(obj);
		}
	}

	@Override
	public void unregister(Observer obj) {
		observers.remove(obj);
		
	}

	@Override
	public void notifyObservers() {
		for (Observer obj : observers) 
			obj.update(this.tasks);
	}

	@Override
	public void disconnect() {
		clientElement.disconnect();
	}
} 

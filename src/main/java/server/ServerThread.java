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

public class ServerThread implements Runnable{
	
    private static final Logger log = Logger.getLogger(ServerThread.class);
	private static final String STATUS_STOPED = "Server stoped";
	private static final String FILE_PATH = "./target/classes/storage/userlist.txt";
	private static final String CHARSET = "UTF-8";
	private final HashMap<String, Method> methods = new HashMap<String, Method>();
    private ServerSocket server;
    private Socket socket;
    protected Collection<TrackerUser> userList;
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
	private ConcurrentMap<String, WorkerThread> peers;

    private InputStreamReader inputStream;

    public ServerThread() throws IOException {
    	server = new ServerSocket(6284);
    	peers = new ConcurrentHashMap<String, WorkerThread>(4);
    	inputStream = new InputStreamReader(new FileInputStream(new File(FILE_PATH)),CHARSET);
    	loadUserList(); 
	}
    
    private void loadUserList() {
    	userList =  new HashSet<TrackerUser>();
    	try (Scanner scanner = new Scanner(inputStream)){
			while (scanner.hasNext()) {
				String userName = scanner.next();
				userList.add(new TrackerUser(userName, 0));
			}
		}
	}

    /**
    * Главный цикл ожидания коннекта и обработки поступивших вызовов
    * Заполняется кэш методов класса VariableEssence
    * Слушаем порт, если поступил вызов, создаем экземпляр  WorkerThread, которому передаем ссылку на себя,
    * на поступивший вызов и кэш методов.
    * запускаем созданный экземпляр при помощи ExecutorService, добавляя его в пул
    * @exception Exception ловим все что не обработано внутри, останавливаем сервер
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
	 * Метод вызывается из экземпляра WorkerThread, при подключении, когда становится известно имя пользователя.
	 * Метод проверяет нет ли пользователя с таким именем в списке, если нет - регистрирует его.
	 * @param worker - WorkerThread, пытающийся зарегистрироваться
	 * @return Если имя не дублировано - true, иначе - false
	 */
	public boolean tryRegister(WorkerThread worker, String userName) {
		return peers.putIfAbsent(userName, worker) == null;
	}
	
	 /**
	* Метод вызывается из экземпляра  WorkerThread, когда пользователь отключается. 
	* Метод снимает пользователя с регистрации в списке активных пользователей.
	* @param wt -  WorkerThread, желающий снять регистрацию
	* @return - true - успешная разрегистрация, false - в списках не было.
	*/
	boolean unregister(WorkerThread wt, String userName) {
		return peers.remove(userName) == wt;
	}
	
	/**
	 * Проверяется, зарегистрирован ли данный WorkerThread в списке клиентов
	 * @param wt - проверяемый WorkerThread
	 * @return true - в списке, false - нет.
	 */
	boolean isRegistered(WorkerThread wt) {
		return peers.containsValue(wt);
	}  
    
	/**
	 * Отключение сервера
	 * Дается 3 секунды на возможно незавершенные операции WorkerThread 
	 * Пробуем свернуть пул
	 * Если серверный сокет не закрыт, пробуем закрыть его
	 * Ждем пока ExecutorService не выкинет флаг isTerminated
	 * Прерываем ServerThread
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
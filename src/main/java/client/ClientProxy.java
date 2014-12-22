package client;

/**
 * @author Shmoylova Kseniya
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientProxy implements java.lang.reflect.InvocationHandler {

	protected Socket socket;
   
	/**
	 * Method for reflect invocation of VariableEssence methods
	 * @see java.lang.reflect.InvocationHandler
	 * Data stream for send method name
	 * Serial stream (Data stream wrapper) for method's arguments and obtaining object resources
	 * @param proxy  (IVariableEssence)Proxy.newProxyInstance
	 * @param method IVariableEssence method
	 * @param args method arguments
	 * @exception throwing read / write to the stream methods
	 */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       
    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    	DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF(method.getName());
       
        if (args != null && args.length > 0) {
        	ObjectOutputStream oos = new ObjectOutputStream(dos);
        	for (Object param : args)
        		oos.writeObject(param);
        }
        dos.flush();
        Object result =null;
        if(method.getReturnType()!=void.class){
        	ObjectInputStream ois = new ObjectInputStream(dis);
        	result = ois.readObject();
        }
        return result;
    }     
}
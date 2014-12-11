package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientProxy implements java.lang.reflect.InvocationHandler {

	protected Socket socket;
   
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
        ObjectInputStream ois = new ObjectInputStream(dis);
        Object result = ois.readObject();
        return result;
    }     
}
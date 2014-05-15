
package pl.edu.pw.elka.mkucharski;

import pl.edu.pw.elka.mkucharski.groovy.GrvT;

import javax.jws.WebService;

@WebService(endpointInterface = "pl.edu.pw.elka.mkucharski.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {

        GrvT g = new GrvT();
        g.setAnother(25);
        g.setSth("dsfs");

        return g.getSth() + " " + g.getAnother();
    }
}


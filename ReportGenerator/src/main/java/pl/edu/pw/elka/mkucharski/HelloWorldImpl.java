
package pl.edu.pw.elka.mkucharski;

import javax.jws.WebService;

@WebService(endpointInterface = "pl.edu.pw.elka.mkucharski.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {
        return "Hello " + text;
    }
}


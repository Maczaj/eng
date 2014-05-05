package pl.edu.pw.elka.mkucharski;

import javax.jws.WebService;

@WebService
public interface HelloWorld {
    String sayHi(String text);
}


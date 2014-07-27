
package pl.edu.pw.elka.mkucharski;

import org.springframework.stereotype.Component;
import pl.edu.pw.elka.mkucharski.groovy.GrvT;

import javax.jws.WebService;

@Component
public class ReportGeneratorImpl implements ReportGenerator {


    public String generateXSLT(String data, String layout) {

        GrvT g = new GrvT();
        g.setAnother(25);
        g.setSth("dsfs");

        return g.getSth() + " " + g.getAnother();
    }
}


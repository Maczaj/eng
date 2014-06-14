
package pl.edu.pw.elka.mkucharski;

import pl.edu.pw.elka.mkucharski.groovy.GrvT;

import javax.jws.WebService;

@WebService(endpointInterface = "pl.edu.pw.elka.mkucharski.ReportGenerator")
public class ReportGeneratorImpl implements ReportGenerator {


    public String generateXSLT(String data, String layout) {

        GrvT g = new GrvT();
        g.setAnother(25);
        g.setSth("dsfs");

        return g.getSth() + " " + g.getAnother();
    }
}


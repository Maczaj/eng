package pl.edu.pw.elka.mkucharski;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ReportGenerator {
    @WebMethod
    String generateXSLT(String data, String layout);
}


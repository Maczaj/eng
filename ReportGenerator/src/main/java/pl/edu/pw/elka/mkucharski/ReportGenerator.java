package pl.edu.pw.elka.mkucharski;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Consumes({"text/xml","text/xml"})
@Produces("application/xml")
public interface ReportGenerator {

    @POST
    @Path("/generator")
    String generateXSLT(String data, String layout);
}


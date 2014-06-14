
package pl.edu.pw.elka.mkucharski;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReportGeneratorImplTest {

    @Test
    public void testSayHi() {
        ReportGeneratorImpl reportGeneratorImpl = new ReportGeneratorImpl();
        String response = reportGeneratorImpl.generateXSLT(null,null);
        assertEquals("ReportGeneratorImpl not properly saying hi", "dsfs 25", response);
    }
}

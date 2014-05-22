package pl.edu.pw.elka.mkucharski;/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 22.05.14
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */

import org.apache.log4j.Logger;

public class Constants {
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String XSLFO_HEADER = "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">";
    public static final String XSLFO_ENDING = "</fo:root>";
    public static final String XSLFO_PAGE_LAYOUT = "<fo:layout-master-set>\n" +
            "  <fo:simple-page-master master-name=\"A4\">\n" +
            "    <fo:region-body />\n" +
            "    <fo:region-after />\n"    +
            "  </fo:simple-page-master>\n" +
            "</fo:layout-master-set>";

    public static final String XSLFO_PAGE_SEQUENCE_HEADER = "<fo:page-sequence master-reference=\"A4\">";
    public static final String XSLFO_PAGE_SEQUENCE_ENDING = "</fo:page-sequence>";

    public static String XSLFO_PAGE_FLOW_HEADER(String regionName){
        return " <fo:flow flow-name=\""+ regionName + "\">";
    }
    public static final String XSLFO_PAGE_FLOW_ENDING = "</fo:flow>";

}

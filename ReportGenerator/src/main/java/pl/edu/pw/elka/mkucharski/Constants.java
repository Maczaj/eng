package pl.edu.pw.elka.mkucharski;/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 22.05.14
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */

public final class Constants {
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String XSLFO_HEADER = "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">";
    public static final String XSLFO_ENDING = "</fo:root>";
    public static final String XSLFO_PAGE_LAYOUT = "<fo:layout-master-set>\n" +
            "  <fo:simple-page-master master-name=\"A4\" margin=\"2cm\">\n" +
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
    public static final String XSLT_HEADER = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
    public static final String XSLT_ENDING = "</xsl:stylesheet>";
    public static final String XSL_TEMPLATE( String xPath ) {
        return " <xsl:template match=\"" + xPath + "\">";
    }
    public static final String XSL_TEMPLATE_ENDING = "</xsl:template>";
    public static final String XSLFO_BLOCK = "<fo:block>";
    public static final String XSLFO_BLOCK_ENDING = "</fo:block>";
    public static final String XSL_VALUEOF(String xPath) {
        return "<xsl:value-of select=\"" + xPath + "\"/>";
    }
    public static final String XSLFO_INSTREAM_FOREIGN_OBJECT = "<fo:instream-foreign-object>";
    public static final String XSLFO_INSTREAM_FOREIGN_OBJECT_ENDING = "</fo:instream-foreign-object>";
    public static final String SVG_ELEMENT = "<svg:svg version=\"1.2\">";
    public static final String SVG_ELEMENT_ENDING = "</svg:svg>";
    public static final String XSLFO_TABLE_AND_CAPTION = "<fo:table-and-caption>";
    public static final String XSLFO_TABLE_AND_CAPTION_ENDING = "</fo:table-and-caption>";
    public static final String XSLFO_TABLE = "<fo:table>";
    public static final String XSLFO_TABLE_ENDING = "</fo:table>";
    public static String XSLFO_TABLE_COLUMN(String width) {
        return "<fo:table-column column-width=\"" + width +"\"/>";
    }
    public static final String XSLFO_TABLE_HEADER = "<fo:table-header>";
    public static final String XSLFO_TABLE_HEADER_ENDING = "</fo:table-header>";
    public static final String XSLFO_TABLE_ROW = "<fo:table-row>";
    public static final String XSLFO_TABLE_ROW_ENDING = "</fo:table-row>";
    public static final String XSLFO_TABLE_CELL = "<fo:table-cell>";
    public static final String XSLFO_TABLE_CELL_ENDING = "</fo:table-cell>";
    public static String XSLFO_BLOCK(String... attrs) {
        StringBuilder sb = new StringBuilder(" <fo:block ");
        for( String attr : attrs) {
            sb.append(attr).append(" ");
        }
        sb.append(">");
        return sb.toString();
    }
    public static final String XSLFO_TABLE_BODY = "<fo:table-body>";
    public static final String XSLFO_TABLE_BODY_ENDING = "</fo:table-body>";
    public static  String XSL_FOR_EACH(String xPath){
        return "<xsl:for-each select=\"" + xPath + "\">";
    }
    public static final String XSL_FOR_EACH_ENDING = "</xsl:for-each>";
    public static String XSLFO_BLOCK_CONTAINER (String height){
        return "<fo:block-container height=\"" + height +"\" >";
    }
    public static String XSLFO_BLOCK_CONTAINER(String...properties){
        StringBuilder sb = new StringBuilder("<fo:block-container ");
        for( String prop : properties) {
            sb.append(prop).append(" ");
        }
        sb.append(">");
        return sb.toString();
    }
    public static final String XSLFO_BLOCK_CONTAINER_ENDING = "</fo:block-container>";

    /**
     * Creates string representation of XSL-FO element, assuming xmlns:fo is used.
     * @param element - name of element
     * @param attrs attributes in form: "attr_name=\"attr_val\""
     * @return concatenated string representation
     */
    public static String XSLFO_ELLEMENT(String element,String... attrs) {
        StringBuilder sb = new StringBuilder("<fo:").append(element).append(" ");

        for(String attr: attrs) {
            sb.append(attr).append(" ");
        }

        return sb.append(" />").toString();
    }

    /**
     * Creates string ending XML node assuming xmlns:fo is used.
     * @param element element name
     * @return
     */
    public static String XSLFO_ELEMENT_ENDING (String element) {
        return new StringBuilder("<fo:").append(element).append("/>").toString();
    }

    public static final String SVG_NAMESPACE = "xmlns:svg=\"http://www.w3.org/2000/svg\"";

    public static String SVG_ELEMENT(String width, String height) {
        StringBuilder sb = new StringBuilder("<svg:svg ").append(SVG_NAMESPACE).append(" width=\"").append(width)
                .append("\" height=\"").append(height).append("\" >");
        return sb.toString();
    }
}

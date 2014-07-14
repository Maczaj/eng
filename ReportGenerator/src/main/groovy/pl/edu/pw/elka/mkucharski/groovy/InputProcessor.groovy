package pl.edu.pw.elka.mkucharski.groovy

import groovy.io.EncodingAwareBufferedWriter
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.Logger

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import static pl.edu.pw.elka.mkucharski.Constants.*

/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 15.05.14
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
class InputProcessor {
    static final Logger logger = Logger.getLogger(InputProcessor.getClass());

    private static final String DEFAULT_DATA_FILTER = "data_filter.xsl";
    private static final String DEFAULT_LAYOUT_FILTER = "layout_filter.xsl"
    private static final String TEMP_DATA = "input.xml";
    private static final String TEMP_LAYOUT = "layout.svg";

    private String dataFilterFileName;
    private String layoutFilterFileName;

    private int usedAdditional;

    /**
     * Creates XSLT that can be applied to XML data to create report.
     * @param dataFileName name of file containing XML data
     * @param layoutFileName name of file containing SVG report layout
     * @return name of file with result XSLT
     */
    public String processInput(String dataFileName,String layoutFileName) {
        return processInput(dataFileName, layoutFileName, "report"+System.currentTimeMillis())
    }

    /**
     * Creates XSLT that can be applied to XML data to create report.
     * @param dataFileName name of file containing XML data
     * @param layoutFileName name of file containing SVG report layout
     * @param resultFileName desired name of file with resulting XSLT
     * @return name of file with XSLT
     */
    String processInput(String dataFileName, String layoutFileName, String resultFileName){
        final List<String> tokenQueue = new ArrayList<>();
          applyXSLT(dataFileName, layoutFileName);

        File resultFile = new File(resultFileName).withWriter("UTF-8",{

//         start processing
            def layoutXML = XmlSlurper.newInstance().parse(layoutFileName);
            def dataXML = XmlSlurper.newInstance().parse(TEMP_DATA);
            addConstantElements(it, tokenQueue, layoutXML);
            logger.info("Data file contains " + (dataXML.children().size()-1) + " additional nodes")

            usedAdditional = 0;
            def masterValues = dataXML.children().findAll { it.name().startsWith('P')}
            logger.info( "Found " + masterValues.size() + " nodes appearing to be master data");

//            TODO: wrzucic w oddzielne fo:block czesc z pierdolami i czesc tabelkowa

            layoutXML.'g'.children().each { node -> processMasterSection(it, node, masterValues)  }

            it.append( SVG_ELEMENT_ENDING );
            it.append( XSLFO_INSTREAM_FOREIGN_OBJECT_ENDING )
            it.append(XSLFO_BLOCK_ENDING).append("\n")
            it.append(XSLFO_BLOCK_CONTAINER_ENDING).append("\n")

            it.append(XSLFO_BLOCK)
//            TODO: przetwarzanie części detail
            processDetailSection(it, dataXML, layoutXML);
            it.append(XSLFO_BLOCK_ENDING)
//            applying ending elements
                for( int i = tokenQueue.size()-1; i >=0 ; --i){
                    it.append(tokenQueue.get(i)).append("\n");
                }


        })


    }

    /**
     * Processes detail section of report, which means creating table with data creating XSL-FO fo:table construction.
     * @param it bufer for output file
     * @param dataXML parsed XML file with data
     * @param layoutXML parsed SVG file with layout
     */
    private void processDetailSection(EncodingAwareBufferedWriter buf, GPathResult dataXML, GPathResult layoutXML) {

        buf.append( XSLFO_TABLE ).append("\n");
        buf.append( XSLFO_TABLE_HEADER ).append("\n");
        buf.append( XSLFO_TABLE_ROW ).append("\n");


        def tableHeader = layoutXML.flowRoot.findAll{it.@'inkex:row' == 0 }
        GPathResult headerRect = layoutXML.children().find { it.@'inkex:row' == 0 }
        tableHeader = tableHeader.sort {it.@'inkex:column'.toInteger()}

        String headerHeight = tableHeader[0].@height;

        tableHeader.each { node->
            buf.append( XSLFO_TABLE_CELL ).append("\n");
            buf.append(XSLFO_BLOCK_CONTAINER("height=\""+headerRect.@height.toString()+ "pt\"","text-align=\"center\"","border-width=\"1pt\"","border-color=\"black\"", "border-style=\"solid\"")).append("\n");
            buf.append( XSLFO_BLOCK ).append( node.text() ).append( XSLFO_BLOCK_ENDING).append("\n");
            buf.append(XSLFO_BLOCK_CONTAINER_ENDING).append("\n")
            buf.append( XSLFO_TABLE_CELL_ENDING).append("\n")
        }
        buf.append(XSLFO_TABLE_ROW_ENDING).append("\n")
        buf.append(XSLFO_TABLE_HEADER_ENDING).append("\n")

//        Find any rectangle from table header

        buf.append(XSLFO_TABLE_BODY).append("\n");
//        rows of data now...
        buf.append(XSL_FOR_EACH("/DOCUMENT//ROWSET/ROW")).append("\n")
        buf.append(XSLFO_TABLE_ROW).append("\n")

//        Hax jeśli w layoucie nie ma specjalnie wiersza dla danych zrobionego
        String tableCellHeight = computeTableCellHeight( layoutXML )
        tableCellHeight = "".equals(tableCellHeight) ? headerHeight : tableCellHeight;

        dataXML.ROWSET.ROW.children().each{ det ->
            buf.append(XSLFO_TABLE_CELL)
            buf.append(XSLFO_BLOCK_CONTAINER("text-align=\"center\"","border-width=\"1pt\"","border-style=\"solid\"","border-color=\"black\"","height=\"" + tableCellHeight + "pt\"")).append("\n")
            buf.append(XSLFO_BLOCK)
            buf.append(XSL_VALUEOF("./" + det.name())).append(XSLFO_BLOCK_ENDING)
            buf.append(XSLFO_BLOCK_CONTAINER_ENDING)
            buf.append(XSLFO_TABLE_CELL_ENDING).append("\n")
        }

        buf.append(XSLFO_TABLE_ROW_ENDING).append("\n")

        buf.append(XSL_FOR_EACH_ENDING).append("\n")

        buf.append(XSLFO_TABLE_BODY_ENDING).append("\n");
        buf.append(XSLFO_TABLE_ENDING).append("\n")
    }

    /**
     * Checks if node belongs to master section and processes it.
     * @param it writer for output file
     */
    private void processMasterSection(EncodingAwareBufferedWriter it, GPathResult node, GPathResult masterValues ){
        switch(node.name()) {
            case "text":
                logger.debug("Processing direct text node");
                String x = node.@x;
                String y = node.@y;
                String style = node.@style;
                it.append("<svg:text x=\"" + x + "\" y=\"" + y + "\" style=\"" + style + "\">\n") ;

                node.tspan.each { tspan ->
                    it.append("<svg:tspan x=\"" + tspan.@x + "\" y=\"" + tspan.@y + "\">" + tspan.text() + "</svg:tspan>\n")
                }
                it.append("</svg:text>")
                break;

            case "image":
                logger.debug("Processing image node")
                it.append("<svg:image x=\"" + node.@x + "\" y=\"" + node.@y + "\" xlink:href=\"" + node.@'xlink:href' + "\" width=\"" + node.@width + "\" height=\"" + node.@height + "\"   />\n")
                break;

            case "flowRoot":
                logger.debug("Processing possible content node");
//            TODO: rozważyć czy w layoucie może wystąpić kwadrat z tekstem, jeśli tak to zawrzeć jego obsługę tutaj
                break;

            case "rect" :
                logger.debug("Processing rectangle node");
                if( node.@'inkex:column' != "" ) {
                    logger.debug("Table rect")
                    return;
                }

                if( usedAdditional > masterValues.size()) {
                    logger.info("No more nodes for master values found");
                    return;
                }

                it.append("<svg:rect x=\"" + node.@x + "\" y=\"" + node.@y + "\" height=\"" + node.@height + "\" width=\"" + node.@width + "\" id=\"" +  node.@id + "\" style=\"" + node.@style + "\" /> \n");
                it.append("<svg:text x=\"" + (node.@x.toDouble()+3) + "\" y=\"" + ( node.@y.toDouble() + node.@height.toDouble()/2  ) +"\">" );
                it.append("<xsl:value-of select=\"/DOCUMENT/" + masterValues[usedAdditional].name() + "\"/>\n");
                it.append("</svg:text>\n");
                usedAdditional++;
                break;

        }
    }

    /**
     * Adds XSL-FO constant elements to the output file and adds their closing tokens to the queue
     * @param it file to which it is written
     * @param tokenQ queue for closing tokens
     */
    private void addConstantElements(EncodingAwareBufferedWriter it, List<String> tokenQ, GPathResult layout){
        it.append(XML_HEADER).append("\n");

        it.append(XSLT_HEADER).append("\n");
        tokenQ.add(XSLT_ENDING);

        it.append( XSL_TEMPLATE("/")).append("\n")
        tokenQ.add(XSL_TEMPLATE_ENDING)

        it.append( XSLFO_HEADER ).append("\n")
        tokenQ.add( XSLFO_ENDING );

        it.append(XSLFO_PAGE_LAYOUT).append("\n").append("\n");

        it.append(XSLFO_PAGE_SEQUENCE_HEADER).append("\n");
        tokenQ.add(XSLFO_PAGE_SEQUENCE_ENDING);

        it.append(XSLFO_PAGE_FLOW_HEADER("xsl-region-body")).append("\n");
        tokenQ.add(XSLFO_PAGE_FLOW_ENDING);

// master section with embedded SVG
        it.append(XSLFO_BLOCK_CONTAINER( computeMasterHeight( layout ) + "pt"));
        it.append("\t").append( XSLFO_BLOCK).append("\n")
        tokenQ.add("\t")

        it.append(XSLFO_INSTREAM_FOREIGN_OBJECT)

        it.append(SVG_ELEMENT)
    }

    /**
     * Applies XSLT transformation for the given files.
     * @param dataFileName name of file containing XML data
     * @param layoutFileName name of file containing SVG report layout
     */
    private void applyXSLT(String dataFileName, String layoutFileName) {
        def factory = TransformerFactory.newInstance()
        String dataXSLT = dataFilterFileName == null? DEFAULT_DATA_FILTER : dataFilterFileName;
        String layoutXSLT = layoutFilterFileName == null ? DEFAULT_LAYOUT_FILTER : layoutFilterFileName;

        logger.info("Applying XSLT: " + dataXSLT + " for data file: " + dataFileName);
        def dataTranformer = factory.newTransformer(new StreamSource( new StringReader(new File(dataXSLT).getText())))
        dataTranformer.transform( new StreamSource( new StringReader(new File(dataFileName).getText("UTF-8"))), new StreamResult(new File(TEMP_DATA)));

        logger.info("Applying XSLT: " + layoutXSLT + " for layout file: " + layoutFileName);
        def layoutTransformer = factory.newTransformer(new StreamSource( new StringReader( new File(layoutXSLT).getText())));
        layoutTransformer.transform( new StreamSource( new StringReader( new File( layoutFileName).getText("UTF-8"))), new StreamResult(new File(TEMP_LAYOUT)));
    }

    /**
     * Computes height of master section
     * @return computer height
     */
    private String computeMasterHeight( GPathResult layout){
        GPathResult result =  layout.children().findAll { it.@'inkex:row' == 0 }
         return result[0].@y.toString();
    }

    /**
     * Computes height of table header cell
     * @param layout
     * @return
     */
    private String computeTableHeaderHeight(GPathResult layout) {
        GPathResult result = layout.children().find {it.@'inkex:row' == 0}
        return result.@height.toString();
    }

    /**
     * Computes height of cell containing data
     * @param layout
     * @return
     */
    private String computeTableCellHeight( GPathResult layout ) {
        GPathResult result = layout.children().find { it.@'inkex:row' == 1 }
        return result.@height.toString();
    }


}

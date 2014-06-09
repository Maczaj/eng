package pl.edu.pw.elka.mkucharski.groovy

import groovy.io.EncodingAwareBufferedWriter
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.Logger
import pl.edu.pw.elka.mkucharski.Constants
import sun.org.mozilla.javascript.internal.xmlimpl.XML

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
            addConstantElements(it, tokenQueue);

//         process layout now
            def layoutXML = XmlSlurper.newInstance().parse(layoutFileName);

//            TODO: wrzucic w oddzielne fo:block czesc z pierdolami i czesc tabelkowa

            layoutXML.'g'.children().each { node ->

                switch(node.name()) {
                    case "text":
                        logger.debug("Processing direct text node");
                        String x = node.@x;
                        String y = node.@y;
                        String style = node.@style;
                        it.append("<svg:text x=\"" + x + "\" y=\"" + y + "\" style=\"" + style + "\">\n") ;
                        tokenQueue.add("</svg:text>");

                        node.tspan.each { tspan ->
                            it.append("<svg:tspan x=\"" + tspan.@x + "\" y=\"" + tspan.@y + "\">" + tspan.text() + "</svg:tspan>\n")
                        }
                        break;

                    case "image":
                        logger.debug("Processing image node")
                        it.append("<svg:image x=\"" + node.@x + "\" y=\"" + node.@y + "\" xlink:href=\"" + node.@'xlink:href' + "\" width=\"" + node.@width + "\" height=\"" + node.@height + "\"   />\n")
                        break;

                    case "flowRoot":
//                        TODO: tutaj moze wchodzi wreszcie jakas dana!!!!
                        break;
                }

            }



//            applying ending elements
                for( int i = tokenQueue.size()-1; i >=0 ; --i){
                    it.append(tokenQueue.get(i)).append("\n");
                }


        })


    }

    /**
     * Adds XSL-FO constant elements to the output file and adds their closing tokens to the queue
     * @param it file to which it is written
     * @param toekenQ queue for closing tokens
     */
    private void addConstantElements(EncodingAwareBufferedWriter it, List<String> tokenQ){
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

        it.append("\t").append( XSLFO_BLOCK).append("\n")
        tokenQ.add("\t")
        tokenQ.add( XSLFO_BLOCK_ENDING )
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


    static void main(){
        InputProcessor ip = new InputProcessor();

        ip.processInput(null,null)
    }

}

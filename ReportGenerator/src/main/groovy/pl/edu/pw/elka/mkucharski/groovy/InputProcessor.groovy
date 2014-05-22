package pl.edu.pw.elka.mkucharski.groovy

import groovy.io.EncodingAwareBufferedWriter
import org.apache.log4j.Logger
import sun.org.mozilla.javascript.internal.xmlimpl.XML

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import pl.edu.pw.elka.mkucharski.Constants

/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 15.05.14
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
class InputProcessor {
    static final Logger logger = Logger.getLogger(InputProcessor.getClass().getName());

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


                for( int i = tokenQueue.size()-1; i >=0 ; --i){
                    it.append(tokenQueue.get(i)).append("\n");
                }


        })


    }

    /**
     * Adds XSL-FO constant elements to the output file and adds their closing tokens to the queue
     * @param it file to which is written
     * @param toekenQ queue for closing tokens
     */
    private void addConstantElements(EncodingAwareBufferedWriter it, List<String> tokenQ){
        it.append(Constants.XML_HEADER).append("\n");
        it.append(Constants.XSLFO_HEADER).append("\n").append("\n");
        tokenQ.add(Constants.XSLFO_ENDING);

        it.append(Constants.XSLFO_PAGE_LAYOUT).append("\n").append("\n");

        it.append(Constants.XSLFO_PAGE_SEQUENCE_HEADER).append("\n");
        tokenQ.add(Constants.XSLFO_PAGE_SEQUENCE_ENDING);

        it.append(Constants.XSLFO_PAGE_FLOW_HEADER("xsl-region-body")).append("\n");
        tokenQ.add(Constants.XSLFO_PAGE_FLOW_ENDING);
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

package pl.edu.pw.elka.mkucharski.groovy

import org.apache.log4j.Logger

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

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
     * TODO: czy to ma zapisać wynik na dysku? czy może po prostu zwrócić DOM/stream XMLa?
     * @param dataFileName name of file with XML data
     * @param layoutFileName name of file with SVG report layout
     */
    void processInput(String dataFileName, String layoutFileName){
          applyXSLT(dataFileName, layoutFileName);
    }

    private applyXSLT(String dataFileName, String layoutFileName) {
        def factory = TransformerFactory.newInstance()
        String dataXSLT = dataFilterFileName == null? DEFAULT_DATA_FILTER : dataFilterFileName;
        String layoutXSLT = layoutFilterFileName == null ? DEFAULT_LAYOUT_FILTER : layoutFilterFileName;

        def dataTranformer = factory.newTransformer(new StreamSource( new StringReader(new File(dataXSLT).getText())))
        dataTranformer.transform( new StreamSource( new StringReader(new File(dataFileName).getText("UTF-8"))), new StreamResult(new File(TEMP_DATA)));

        def layoutTransformer = factory.newTransformer(new StreamSource( new StringReader( new File(layoutXSLT).getText())));
        layoutTransformer.transform( new StreamSource( new StringReader( new File( layoutFileName).getText("UTF-8"))), new StreamResult(new File(TEMP_LAYOUT)));
    }


    static void main(){
        InputProcessor ip = new InputProcessor();

        ip.processInput(null,null)
    }

}

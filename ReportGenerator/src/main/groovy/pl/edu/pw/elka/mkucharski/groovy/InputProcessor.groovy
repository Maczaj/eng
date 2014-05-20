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

    static def xslt = '''<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                            <xsl:template match="/DOCUMENT//ROWSET/ROW[position()>1]">
                            </xsl:template>
                        </xsl:stylesheet>'''

    /**
     * TODO: czy to ma zapisać wynik na dysku? czy może po prostu zwrócić DOM/stream XMLa?
     * @param dataFileName name of file with XML data
     * @param layoutFileName name of file with SVG report layout
     */
    void processInput(String dataFileName, String layoutFileName){
       File xslt = new File("data_filter.xsl");
        File input = new File("../employees.xml");


       def factory = TransformerFactory.newInstance();
       def transformer = factory.newTransformer(new StreamSource(new StringReader(xslt.getText("UTF-8"))));
        transformer.transform(new StreamSource(new StringReader(input.getText("UTF-8"))), new StreamResult(System.out));
    }

    static void main(){
        InputProcessor ip = new InputProcessor();

        ip.processInput(null,null)
    }

}

package pl.edu.pw.elka.mkucharski.groovy

import groovy.io.EncodingAwareBufferedWriter
import groovy.util.slurpersupport.GPathResult
import org.apache.cxf.helpers.IOUtils
import org.apache.log4j.Logger
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.util.regex.Pattern

import static pl.edu.pw.elka.mkucharski.Constants.*

/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 15.05.14
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
@Component
class InputProcessor {
    static final Logger logger = Logger.getLogger(InputProcessor.getClass().getName());

    private static final String DEFAULT_DATA_FILTER = "data_filter.xsl";
    private static final String DEFAULT_LAYOUT_FILTER = "layout_filter.xsl"
    private static final String TEMP_DATA = "input.xml";
    private static final String TEMP_LAYOUT = "layout.svg";

    private Properties defaults;

    private String dataFilterFileName;
    private String layoutFilterFileName;


    private int usedAdditional;

    /**
     * Creates XSLT that can be applied to XML data to create report.
     * @param dataFileName name of file containing XML data
     * @param layoutFileName name of file containing SVG report layout
     * @return name of file with resulting XSLT
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
        applyXSLT(dataFileName, layoutFileName);
        loadDefaults()

        InputStream pageStream = InputProcessor.getClass().getResourceAsStream("/page.xml");
        StringBuilder pageTemplate = new StringBuilder(IOUtils.toString(pageStream,"UTF-8"));

//         start processing
            def layoutXML = XmlSlurper.newInstance().parse(layoutFileName);
            def dataXML = XmlSlurper.newInstance().parse(TEMP_DATA);
            logger.info("Data file contains " + (dataXML.children().size()-1) + " additional nodes")

            usedAdditional = 0;
            def masterValues = dataXML.children().findAll { it.name().startsWith('P')}
            logger.info( "Found " + masterValues.size() + " nodes appearing to be master data");

//            master section (SVG part)
            StringBuilder master = new StringBuilder();
            master.append(SVG_ELEMENT("20cm", computeMasterHeight(layoutXML)+"pt"))
            layoutXML.g.children().each { node -> processMasterSection(master, node, masterValues)  }
            master.append("</svg:svg>")

//        Compute values for attributes
            setHeaderProperties(layoutXML);
        setBodyProperties(layoutXML);

//        Store computed attribute values in result
            for(String key : defaults.stringPropertyNames() ) {
                String _key = '${' + key + '}'
                int i;
                while(( i = pageTemplate.indexOf(_key) ) != -1 ) {
                    pageTemplate.replace(i, i + _key.length() , defaults.getProperty(key));
                }
            }

//        format page template with svg part
            new File(resultFileName).withWriter("UTF-8", {it.write(String.format(pageTemplate.toString(),master.toString()))} )
        logger.info("Processing finished")
    }

    /**
     * Sets properties of table's data cells. If layout doesn't contain such, default values are left.
     * @param layout
     */
    void setBodyProperties(GPathResult layout) {
        

    }
/**
     * Sets properties for table header cell
     * @param layout GPathResult for report layout root
     */
    void setHeaderProperties(GPathResult layout) {
        def cell =  layout.children().find { it.@'inkex:row' == 0 }

        String style = cell.@style;
        Map<String,String> mapper = new HashMap<>();
        for(String sp : style.split(";")) {
            String[] tokenized = sp.split(":");
            mapper.put(tokenized[0],tokenized[1]);
        }

        defaults.setProperty('header.color.background', mapper.get("fill") );
        defaults.setProperty('border.color',mapper.get("stroke"))
        String borderWidth = mapper.get("stroke-width")
        defaults.setProperty('border.width',borderWidth.substring(0,borderWidth.indexOf("px")))

        mapper.clear();

        def text = layout.flowRoot.find{ it.@'inkex:row' == 0 && it.@'inkex:column' == 0}
        style = text.@style;
        for(String sp : style.split(";")) {
            String[] tokenized = sp.split(":");
            mapper.put(tokenized[0],tokenized[1]);
        }

        defaults.setProperty('header.color.color', mapper.get("fill"));
        String textSize = mapper.get("font-size")
        defaults.setProperty('header.font.size', textSize.substring(0,textSize.indexOf("px")))
        defaults.setProperty('header.font.family', mapper.get("font-family"))
        defaults.setProperty('header.font.weight', mapper.get("font-weight"))
    }
/**
     * Loads default report properties from resource
     */
    void loadDefaults() {
        defaults = new Properties();
        defaults.load(this.getClass().getResourceAsStream("/defaults.properties"));
        logger.info("Default properties successfully loaded")
    }

    /**
     * Checks if node belongs to master section and processes it.
     * @param it stringbuilder to store master part of report
     */
    private void processMasterSection(StringBuilder it, GPathResult node, GPathResult masterValues ){
        switch(node.name()) {
//            TODO: mapowanie elementow zgodnie z nowymi zalozeniami, tj. styl nie CSSowy etc.
            case "text":
                logger.debug("Processing direct text node");
                String x = node.@x;
                String y = node.@y;
                it.append("<svg:text x=\"" + x + "\" y=\"" + y + "\" " + mapStyle( (String)node.@style) + " >") ;

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

//            rect not belonging to table means its data field
            case "rect" :
                logger.debug("Processing rectangle node");
                if( StringUtils.hasText((String)node.@'inkex:column')  ) {
                    logger.debug("Table rect")
                    return;
                }

                if( usedAdditional > masterValues.size()) {
                    logger.info("No more nodes for master values found");
                    return;
                }

                it.append("<svg:rect x=\"" + node.@x + "\" y=\"" + node.@y + "\" height=\"" + node.@height + "\" width=\"" + node.@width + "\" id=\"" +  node.@id + "\" " + mapStyle((String)node.@style ) + " /> \n");
                it.append("<svg:text x=\"" + (node.@x.toDouble()+3) + "\" y=\"" + ( node.@y.toDouble() + node.@height.toDouble()/2  ) +"\">" );
                it.append("<xsl:value-of select=\"/DOCUMENT/" + masterValues[usedAdditional].name() + "\"/>\n");
                it.append("</svg:text>\n");
                usedAdditional++;
                break;
        }
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

    /**
     * This method transforms CSS-style attributes into form acceptable by FOP delivered with ApEx
     * @param style String with style expression
     */
    private String mapStyle( String style) {
        StringBuilder sb = new StringBuilder();
        String[] tokenizedStyle = style.split(";");

        for(String attr : tokenizedStyle ) {
            String[] splitted = attr.split(":");
            sb.append(splitted[0]).append("=\"").append(splitted[1]).append("\" ");
        }
        return sb.toString();
    }

}

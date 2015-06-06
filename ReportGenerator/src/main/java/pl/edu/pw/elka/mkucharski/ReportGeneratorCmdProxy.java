package pl.edu.pw.elka.mkucharski;/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 20.05.14
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.codehaus.groovy.syntax.Numbers;
import pl.edu.pw.elka.mkucharski.groovy.InputProcessor;

import java.util.HashMap;
import java.util.Map;

public class ReportGeneratorCmdProxy {
    private static final Logger logger = Logger.getLogger(ReportGeneratorCmdProxy.class.getName());


    public static void main() {
        BasicConfigurator.configure();

        InputProcessor processor = new InputProcessor();
        logger.info("Attempting to generate report");
        processor.processInput( "../sth-pdf.xml", "../ir_md.svg", "act", "test");
        processor.setMasterCount(1);
    }
    //        arg1: xml z danymi, arg2: svg z layoutem, arg3: outputDir, arg4: suffix wynikowych plików
//    opcje: -cw auto lub fixed, -grp 0 lub 1
    public static void main( String[] args) {
        BasicConfigurator.configure();

        if(args.length < 3 ) {
//            tutaj jakiś tekst o tym, że źle wywołane, etc.
            throw new IllegalArgumentException("Mandatory arguments not given!");
        }
//        String outputSuffix = args.length >= 4 ? args[3] : Long.toString(System.currentTimeMillis());
        InputProcessor processor = new InputProcessor();

        Map<String,String> argMap = new HashMap<>();
        for(int i = 4 ; i < args.length; i+=2) {
            argMap.put(args[i], args[i+1]);
        }

        if(argMap.containsKey("-grp")) {
            processor.setGrouping("1".equals(argMap.get("-grp")) ? true : false);
        }
        if( argMap.containsKey("-cw") ) {
            if("auto".equals(argMap.get("-cw"))) {
                processor.setAutoWidth(true);
            } else if("fixed".equals(argMap.get("-cw"))) {
                processor.setAutoWidth(false);
            } else {
                logger.warn("Unknown value for cw parameter. Argument ignored");
            }
        }

        if(argMap.containsKey("-md")) {
            String[] arg = argMap.get("-md").split(";");
            processor.setMasterCount(arg.length);
                for (String name : arg ) {
                    processor.getMasterDisplayNames().add(name);
                }
        }


//        processor.setMasterCount(1);
//        processor.processInput( "../sth-pdf.xml", "../ir_md.svg", "act", "test");



        logger.info("Attempting to generate report");
        processor.processInput( args[0], args[1], args[2], args[3]);
    }
}

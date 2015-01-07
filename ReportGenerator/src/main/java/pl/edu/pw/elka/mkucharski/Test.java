package pl.edu.pw.elka.mkucharski;/**
 * Created with IntelliJ IDEA.
 * User: maczaj
 * Date: 20.05.14
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import pl.edu.pw.elka.mkucharski.groovy.InputProcessor;

public class Test {
    private static final Logger logger = Logger.getLogger(Test.class.getName());


    public static void main( String[] args) {
        BasicConfigurator.configure();

        InputProcessor processor = new InputProcessor();
        logger.info("Attempting to generate report");
        processor.processInput( "../md-test.xml", "../inny.svg", "result.xsl");
    }
}

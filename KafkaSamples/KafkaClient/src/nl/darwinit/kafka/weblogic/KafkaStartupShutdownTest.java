/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 *
 * Class to test the mechanism of starting up and shutting down Kafka infrastructure based upon KafkaStartupClass
 * and KafkaShutdownClass.
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.weblogic;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.server.ZooKeeperDriver;


public class KafkaStartupShutdownTest {
    private static Log log = new Log(KafkaStartupShutdownTest.class);
    private static final int TEN_MINUTES_IN_MS=10*60*1000;
    private static final int FIVE_MINUTES_IN_MS=5*60*1000;
    private static final int ONE_MINUTE_IN_MS=60*1000;
    private static ZooKeeperDriver zooKeeperDriver;
    

    public KafkaStartupShutdownTest() {
        super();
    }

          
    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        KafkaStartupClass.main(args);       
        try {
            log.info(methodName,"Going into sleep...");
            Thread.sleep(TEN_MINUTES_IN_MS);
        } catch (InterruptedException e) {
            log.error(methodName, "Failed to sleep...", e);
        }
        log.info(methodName,"Shutdown Zookeeper...");
        KafkaShutdownClass.main(args);
        log.end(methodName);
    }
}

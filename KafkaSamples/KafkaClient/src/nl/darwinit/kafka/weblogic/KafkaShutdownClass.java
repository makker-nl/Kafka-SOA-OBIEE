/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 *
 * Shutdown class to be able to shutdown a Apache Kafka infrastructure under Weblogic, that is started
 * from the KafkaStartupClass.
 * See https://docs.oracle.com/middleware/1221/wls/START/overview.htm#START244 for starting up and shutting down 
 * Weblogic servers.
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.weblogic;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.server.ZooKeeperDriver;


public class KafkaShutdownClass {
    private static Log log = new Log(KafkaShutdownClass.class);
       

    public KafkaShutdownClass() {
        super();
    }
    

    public static void shutdownZookeeper() {
        final String methodName = "shutdownZookeeper";
        log.start(methodName);
        ZooKeeperDriver zooKeeperDriver = KafkaStartupClass.getZooKeeperDriver();
        zooKeeperDriver.shutdown();        
        log.end(methodName);
    }
 
    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        log.info(methodName,"Shutdown Zookeeper...");
        shutdownZookeeper();        
        log.end(methodName);
    }

    
}

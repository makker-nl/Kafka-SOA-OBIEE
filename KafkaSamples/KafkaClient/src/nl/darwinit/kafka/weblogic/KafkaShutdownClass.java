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

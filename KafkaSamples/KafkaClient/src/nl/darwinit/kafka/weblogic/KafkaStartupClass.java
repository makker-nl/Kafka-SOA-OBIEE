package nl.darwinit.kafka.weblogic;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.server.KafkaServerStarter;


import nl.darwinit.kafka.server.ZooKeeperDriver;
 

public class KafkaStartupClass {
    private static Log log = new Log(KafkaStartupClass.class);
    private static final int TEN_MINUTES_IN_MS=10*60*1000;
    private static final int FIVE_MINUTES_IN_MS=5*60*1000;
    private static final int ONE_MINUTE_IN_MS=60*1000;
    private static ZooKeeperDriver zooKeeperDriver;
    

    public KafkaStartupClass() {
        super();
    }

    public static void startZookeeper() {
        final String methodName = "startZookeeper";
        log.start(methodName);
        ZooKeeperDriver zooKeeperDriver = new ZooKeeperDriver();
        setZooKeeperDriver(zooKeeperDriver);
        zooKeeperDriver.start();
        
        log.end(methodName);
    }

    public static void startKafkaServer(int serverNr) {
        final String methodName = "startKafkaServer(int) ";
        log.start(methodName);
        log.info(methodName, "Start KafkaServer " + serverNr);
        KafkaServerStarter kafkaServerStarter = new KafkaServerStarter();
        kafkaServerStarter.start();
        log.end(methodName);
    }

 
    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        startZookeeper();
        log.end(methodName);
    }

    public static void setZooKeeperDriver(ZooKeeperDriver zooKeeperDriver) {
        KafkaStartupClass.zooKeeperDriver = zooKeeperDriver;
    }

    public static ZooKeeperDriver getZooKeeperDriver() {
        return zooKeeperDriver;
    }
}

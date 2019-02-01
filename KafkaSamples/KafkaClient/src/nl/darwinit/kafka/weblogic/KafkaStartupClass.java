/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 *
 * Startup class to be able to startup a Apache Kafka infrastructure under Weblogic
 * See https://docs.oracle.com/middleware/1221/wls/START/overview.htm#START244 for starting up and shutting down 
 * Weblogic servers.
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.weblogic;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.server.KafkaServerDriver;


import nl.darwinit.kafka.server.ZooKeeperDriver;


public class KafkaStartupClass {
    private static Log log = new Log(KafkaStartupClass.class);
    private static final int TEN_MINUTES_IN_MS = 10 * 60 * 1000;
    private static final int FIVE_MINUTES_IN_MS = 5 * 60 * 1000;
    private static final int ONE_MINUTE_IN_MS = 60 * 1000;
    private static ZooKeeperDriver zooKeeperDriver;
    private static KafkaServerDriver kafkaServerDriver;


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

    public static void startKafkaServer() {
        final String methodName = "startKafkaServer() ";
        log.start(methodName);
        log.info(methodName, "Start KafkaServer");
        KafkaServerDriver kafkaServerDriver = new KafkaServerDriver();
        setKafkaServerDriver(kafkaServerDriver);
        kafkaServerDriver.start();
        log.end(methodName);
    }


    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        startZookeeper();
        startKafkaServer();
        log.end(methodName);
    }

    public static void setZooKeeperDriver(ZooKeeperDriver zooKeeperDriver) {
        KafkaStartupClass.zooKeeperDriver = zooKeeperDriver;
    }

    public static ZooKeeperDriver getZooKeeperDriver() {
        return zooKeeperDriver;
    }

    public static void setKafkaServerDriver(KafkaServerDriver kafkaServerDriver) {
        KafkaStartupClass.kafkaServerDriver = kafkaServerDriver;
    }

    public static KafkaServerDriver getKafkaServerDriver() {
        return kafkaServerDriver;
    }
}

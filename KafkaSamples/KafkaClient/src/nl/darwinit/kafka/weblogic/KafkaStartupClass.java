package nl.darwinit.kafka.weblogic;

import nl.darwinit.kafka.server.KafkaServerStarter;
import nl.darwinit.kafka.server.ZooKeeperStarter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class KafkaStartupClass {
    private static String className = KafkaStartupClass.class.getName();
    private static Logger logger = LogManager.getLogger(className);

    public KafkaStartupClass() {
        super();
    }

    public void startZookeeper() {
        final String methodName = "startZookeeper";
        logStart(methodName);
        ZooKeeperStarter zooKeeperStarter = new ZooKeeperStarter();
        zooKeeperStarter.start();
        logEnd(methodName);
    }

    public void startKafkaServer(int serverNr) {
        final String methodName = "startKafkaServer(int) ";
        logStart(methodName);
        log(methodName, "Start KafkaServer " + serverNr);
        KafkaServerStarter kafkaServerStarter = new KafkaServerStarter();
        kafkaServerStarter.start();
        logEnd(methodName);
    }

    public static void log(String methodName, String text) {
        logger.debug(className + "." + methodName + ": " + text);
    }

    public static void logStart(String methodName) {
        log(methodName, "Start");
    }

    public static void logEnd(String methodName) {
        log(methodName, "End");
    }

    public static void main(String[] args) {
        final String methodName = "main";
        logStart(methodName);
        KafkaStartupClass kafkaStartupClass = new KafkaStartupClass();
        kafkaStartupClass.startZookeeper();
        kafkaStartupClass.startKafkaServer(0);
        logEnd(methodName);
    }
}

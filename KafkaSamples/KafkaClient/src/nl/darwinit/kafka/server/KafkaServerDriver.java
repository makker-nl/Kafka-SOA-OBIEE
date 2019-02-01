/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 * 
 * Class to get a hold of ZooKeeper Server as an Observable. 
 * To be able to have a ZooKeerp Server in a separate thread and be able to send it a shutdown signal.
 * 
 * Used:
 * https://stackoverflow.com/questions/9286054/is-it-possible-to-start-a-zookeeper-server-instance-in-process-say-for-unit-tes
 * https://www.programcreek.com/java-api-examples/index.php?api=kafka.server.KafkaServer
 * https://grokonez.com/java-integration/distributed-system/start-apache-kafka#2_Start_a_Kafka_server
 * https://www.programcreek.com/java-api-examples/index.php?api=kafka.metrics.KafkaMetricsReporter
 * 
 * History
 * 2019-01-25 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.server;

import java.io.IOException;

import java.util.List;
import java.util.Observable;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.KafkaServerDriverProperties;
import nl.darwinit.kafka.properties.Properties;
import nl.darwinit.kafka.properties.PropertiesFactory;

public class KafkaServerDriver extends Observable {
    private static Log log = new Log(KafkaServerDriver.class);
    public final static String PRP_BRKR_ID = "broker.id";
    private boolean shutdownKafkaServers = false;
    private KafkaServerDriverProperties ksdProperties;

    /**
     * Default Constructor
     */
    public KafkaServerDriver() {
        super();
        final String methodName = "KafkaServerDriver()";
        try {
            KafkaServerDriverProperties ksdProperties = PropertiesFactory.getKSDProperties();
            this.setKsdProperties(ksdProperties);

        } catch (IOException e) {
            log.error(methodName, "Failed to load properties!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Shutdown all KafkaServers
     */
    // Shutdown all KafkaServers
    public void shutdown() {
        final String methodName = "shutdown";
        log.start(methodName);
        setShutdownKafkaServers(true);
        log.info(methodName, "Notify Observers to shutdown!");
        this.setChanged();
        this.notifyObservers();
        log.end(methodName);
    }
    
    /**
     * Add a KafkaServer from properties
     * @param ksProperties
     */
    public void addKafkaServer(Properties ksProperties) {
        final String methodName = "addKafkaServer";
        log.start(methodName);
        KafkaObserver kafkaServer = new KafkaObserver(this, ksProperties);
        Thread newKSThread = new Thread(kafkaServer);
        newKSThread.setName("KafkaServer" + ksProperties.getProperty(PRP_BRKR_ID));
        kafkaServer.setKsThread(newKSThread);
        newKSThread.start();

        log.end(methodName);
    }
    
    /**
     * Add a KafkaServer
     * @param kafkaServerName
     */
    public void addKafkaServer(String kafkaServerName) {
        final String methodName = "addKafkaServer(String)";
        log.start(methodName);
        try {
            Properties serverProperties = ksdProperties.getServerProperties(kafkaServerName);

            if (serverProperties.getBoolValue("startupEnabled")) {
                log.info(methodName, "Start KafkaServer " + kafkaServerName);
                String serverPropertiesFileName = serverProperties.getStringValue("propertyfile");
                log.debug(methodName, "KafkaServer propertyfile: " + serverPropertiesFileName);
                String brokerId = serverProperties.getStringValue("id");
                Properties ksProperties = null;
                if (serverPropertiesFileName != null) {
                    ksProperties = PropertiesFactory.getKSProperties(serverPropertiesFileName);
                } else {
                    ksProperties = PropertiesFactory.getKSProperties();
                }
                addKafkaServer(ksProperties);
            } else {
                log.info(methodName, "KafkaServer " + kafkaServerName + " has startupEnabled == false!");

            }
        } catch (IOException e) {
            log.error(methodName, "Failed to load properties!", e);
            throw new RuntimeException(e);
        }
        log.end(methodName);
    }


    /**
     * Add Default KafkaServer
     */
    public void addKafkaServer() {
        final String methodName = "addKafkaServer()";
        try {
            Properties ksProperties = PropertiesFactory.getKSProperties();
            addKafkaServer(ksProperties);
        } catch (IOException e) {
            log.error(methodName, "Failed to load properties!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Start KafkaServers
     */
    public void start() {
        final String methodName = "start";
        log.start(methodName);
        for (String kafkaServerName : ksdProperties.getKafkaServerList()) {
            log.debug(methodName, "Start KafkaServer: " + kafkaServerName);
            addKafkaServer(kafkaServerName);
        }
        //addKafkaServer();
        log.end(methodName);
    }

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        KafkaServerDriver kafkaServerDriver = new KafkaServerDriver();
        kafkaServerDriver.start();
        log.end(methodName);
    }


    public void setShutdownKafkaServers(boolean shutdownKafkaServers) {
        this.shutdownKafkaServers = shutdownKafkaServers;
    }

    public boolean isShutdownKafkaServers() {
        return shutdownKafkaServers;
    }

    public void setKsdProperties(KafkaServerDriverProperties ksdProperties) {
        this.ksdProperties = ksdProperties;
    }

    public KafkaServerDriverProperties getKsdProperties() {
        return ksdProperties;
    }
}

/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 * 
 * ZooKeeperObserver is an Observer of the ZooKeeperDriver class, 
 * to be able to start a ZooKeeper in a seperate thread and have it shutdown based upon an update signal.
 * This class is based on https://www.programcreek.com/java-api-examples/?code=txazo/ZooKeeper/ZooKeeper-master/src/main/java/org/apache/zookeeper/server/ZooKeeperServerMain.java.
 * Used to be able to extend and adapt it. 
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.server;

import java.io.IOException;

import java.util.Observable;
import java.util.Observer;

import kafka.controller.KafkaController;

import kafka.metrics.KafkaMetricsReporter;

import kafka.server.AdminManager;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.Properties;

import org.apache.kafka.common.utils.SystemTime;

import scala.Option;

import scala.collection.Seq;
import scala.collection.mutable.ArraySeq;


/**
 * This class starts and runs a standalone ZooKeeperServer.
 */
public class KafkaObserver implements Observer, Runnable {
    private static final Log log = new Log(KafkaObserver.class);
    private Properties ksProperties;
    private Thread ksThread;
    private KafkaServerDriver kafkaServerDriver;
    private KafkaServer kafkaServer;


    public KafkaObserver() {
        super();
    }

    public KafkaObserver(Observable kafkaServerDriver, Properties ksProperties) {
        super();
        final String methodName = "KafkaObserver(Observable, Properties)";
        log.start(methodName);
        this.setKsProperties(ksProperties);
        if (kafkaServerDriver instanceof KafkaServerDriver) {
            log.info(methodName,
                     "Add observer " + this.getClass().getName() + " to observable " +
                     kafkaServerDriver.getClass().getName());
            setKafkaServerDriver((KafkaServerDriver) kafkaServerDriver);
            kafkaServerDriver.addObserver(this);
        }
        log.end(methodName);
    }

    /**
     * Run from a ServerConfig.
     * @param config ServerConfig to use.
     * @throws IOException
     */
    public void runFromProperties(Properties ksProperties) throws IOException {
        final String methodName = "runFromProperties";
        log.start(methodName);
        log.info(methodName, "Starting server");
        KafkaConfig config = KafkaConfig.fromProps(ksProperties);
        //VerifiableProperties verifiableProps = new VerifiableProperties(ksProperties);
        Seq<KafkaMetricsReporter> reporters = new ArraySeq<KafkaMetricsReporter>(0);
        // Seq<KafkaMetricsReporter> reporters = (Seq<KafkaMetricsReporter>) KafkaMetricsReporter$.MODULE$.startReporters(verifiableProps);
        KafkaServer kafkaServer = new KafkaServer(config, new SystemTime(), Option.apply("prefix"), reporters);
        setKafkaServer(kafkaServer);
        kafkaServer.startup();
        log.end(methodName);
    }

    /**
     * Shutdown the serving instance
     */
    public void shutdown() {
        final String methodName = "shutdown";
        log.start(methodName);
        log.info(methodName, "Let me shutdown " + getKsThread().getName());
        KafkaServer kafkaServer = getKafkaServer();
        kafkaServer.shutdown();
        log.end(methodName);
    }

    @Override
    public void update(Observable o, Object arg) {
        final String methodName = "update(Observable,Object)";
        log.start(methodName);
        Thread ksThread = getKsThread();
        log.info(methodName, ksThread.getName() + " - Got status update from Observable!");
        KafkaServerDriver ksDriver = getKafkaServerDriver();
        if (ksDriver.isShutdownKafkaServers()) {
            log.info(methodName, ksThread.getName() + " - Apparently I´ve got to shutdown myself!");
            shutdown();
        } else {
            log.info(methodName, ksThread.getName() + " - Don't know what to do with this status update!");
        }
        log.end(methodName);
    }

    @Override
    public void run() {
        final String methodName = "run";
        log.start(methodName);
        try {
            runFromProperties(getKsProperties());
        } catch (IOException ioe) {
            log.error(methodName, "Run failed!", ioe);
        }
        log.end(methodName);

    }

    public void setKsProperties(Properties ksProperties) {
        this.ksProperties = ksProperties;
    }

    public Properties getKsProperties() {
        return ksProperties;
    }

    public void setKsThread(Thread ksThread) {
        this.ksThread = ksThread;
    }

    public Thread getKsThread() {
        return ksThread;
    }

    public void setKafkaServer(KafkaServer kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    public KafkaServer getKafkaServer() {
        return kafkaServer;
    }

    public void setKafkaServerDriver(KafkaServerDriver kafkaServerDriver) {
        this.kafkaServerDriver = kafkaServerDriver;
    }

    public KafkaServerDriver getKafkaServerDriver() {
        return kafkaServerDriver;
    }
}

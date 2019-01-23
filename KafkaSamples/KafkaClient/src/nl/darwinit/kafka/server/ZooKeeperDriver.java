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
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.server;

import java.io.IOException;

import java.util.Observable;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.PropertiesFactory;
import nl.darwinit.kafka.properties.ZooKeeperProperties;

 
public class ZooKeeperDriver extends Observable {
    private static Log log = new Log(ZooKeeperDriver.class);
    private boolean shutdownZookeepers = false;

    public ZooKeeperDriver() {
        super();
    }


   // Shutdown all Zookeeper
    public void shutdown() {
        final String methodName = "shutdown";
        log.start(methodName);
        setShutdownZookeepers(true);
        log.info(methodName, "Notify Observers to shutdown!");
        this.setChanged();
        this.notifyObservers();
        log.end(methodName);
    }


    public void addZookeeper() {
        final String methodName = "addZookeeper";
        log.start(methodName);
        try {
            ZooKeeperProperties zkProperties = PropertiesFactory.getZKProperties();
            ZookeeperObserver zooKeeperServer = new ZookeeperObserver(this, zkProperties);
            Thread newZooKeeperThread = new Thread(zooKeeperServer);
            zooKeeperServer.setMyThread(newZooKeeperThread);
            newZooKeeperThread.start();
        } catch (IOException e) {
            log.error(methodName, "ZooKeeper Failed", e);
        }
        log.end(methodName);
    }

    public void start() {
        final String methodName = "start";
        log.start(methodName);
        addZookeeper();
        log.end(methodName);
    }


    public void setShutdownZookeepers(boolean shutdownZookeepers) {
        this.shutdownZookeepers = shutdownZookeepers;
    }

    public boolean isShutdownZookeepers() {
        return shutdownZookeepers;
    }

    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        ZooKeeperDriver zooKeeperDriver = new ZooKeeperDriver();
        zooKeeperDriver.start();
        log.end(methodName);
    }
}

package nl.darwinit.kafka.server;

import java.io.IOException;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.PropertiesFactory;
import nl.darwinit.kafka.properties.ZooKeeperProperties;


/**
 * https://stackoverflow.com/questions/9286054/is-it-possible-to-start-a-zookeeper-server-instance-in-process-say-for-unit-tes
 * https://www.programcreek.com/java-api-examples/index.php?api=kafka.server.KafkaServer
 */
public class ZooKeeperStarter {
    private static Log log = new Log(ZooKeeperStarter.class); 

    public ZooKeeperStarter() {
        super();
    }

    public void stop() {

    }

    public void start() {
        final String methodName = "start";
        log.start(methodName);
        try {
            ZooKeeperProperties zkProperties = PropertiesFactory.getZKProperties();
            EmbeddedZookeeperServer zooKeeperServer = new EmbeddedZookeeperServer();
            new Thread() {
                public void run() {
                    try {
                        zooKeeperServer.runFromProperties(zkProperties);
                    } catch (IOException e) {
                        log.error(methodName, "ZooKeeper Failed", e);
                    }
                }
            }.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.end(methodName);
    }

    

    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        ZooKeeperStarter zooKeeperStarter = new ZooKeeperStarter();
        zooKeeperStarter.start();
        log.end(methodName);
    }
}

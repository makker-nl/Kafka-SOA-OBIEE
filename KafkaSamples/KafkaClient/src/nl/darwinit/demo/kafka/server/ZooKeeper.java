package nl.darwinit.demo.kafka.server;

import java.io.IOException;

import java.util.Properties;

import nl.darwinit.demo.kafka.properties.PropertiesFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;


/**
 * https://stackoverflow.com/questions/9286054/is-it-possible-to-start-a-zookeeper-server-instance-in-process-say-for-unit-tes
 * https://www.programcreek.com/java-api-examples/index.php?api=kafka.server.KafkaServer
 */
public class ZooKeeper {
    protected  Logger logger = LogManager.getLogger(this.getClass());
    public ZooKeeper() {
        super();
    }


    public  void start() {
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            Properties startupProperties = PropertiesFactory.getZKProperties();
            quorumConfiguration.parseProperties(startupProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ZooKeeperServerMain zooKeeperServer = new ZooKeeperServerMain();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);

        new Thread() {
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                    logger.error("ZooKeeper Failed", e);
                }
            }
        }.start();

    }

    public static void main(String[] args) {
        ZooKeeper zooKeeper = new ZooKeeper();
        zooKeeper.start();
    }
}

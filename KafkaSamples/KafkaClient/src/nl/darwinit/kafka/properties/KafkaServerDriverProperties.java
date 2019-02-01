package nl.darwinit.kafka.properties;
/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 *
 * Class to parse and hold properties applicable to ZooKeeperServer.
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.List;

import nl.darwinit.kafka.logging.Log;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;


public class KafkaServerDriverProperties {

    private static Log log = new Log(KafkaServerDriverProperties.class);
    private List<String> kafkaServerList;
    private HashMap<String, Properties> serverPropertiesMap = new HashMap<String, Properties> (); 

    /**
     * Set Properties
     * @param ksdProperties
     * @throws UnknownHostException
     */
    public void setProperties(Properties ksdProperties) throws UnknownHostException {
        final String methodName = "setProperties(Properties)";
        /*
        server0.id=0
        server0.propertyfile=server.properties
        server0.startupEnabled=true
        server1.id=1
        server1.propertyfile=server1.properties
        server1.startupEnabled=true

         */
        log.start(methodName);
        List<String> kafkaServerList = ksdProperties.getListValue("kafkaservers");
        setKafkaServerList(kafkaServerList);
        for (String kafkaServerName : kafkaServerList) {
            log.debug(methodName, "Set properties for kafkaServer: " + kafkaServerName);
            Properties serverProperties = new Properties();
            serverProperties.put("id", ksdProperties.getStringValue(kafkaServerName + ".id"));
            serverProperties.put("propertyfile", ksdProperties.getStringValue(kafkaServerName + ".propertyfile"));
            serverProperties.put("startupEnabled", ksdProperties.getStringValue(kafkaServerName + ".startupEnabled"));
            this.addServerProperties(kafkaServerName, serverProperties);
        }
        log.end(methodName);
    }

    public KafkaServerDriverProperties(Properties ksdProperties) throws UnknownHostException {
        super();
        final String methodName = "KafkaServerDriverProperties(Properties)";
        log.start(methodName);
        setProperties(ksdProperties);
        log.end(methodName);
    }


    public void setKafkaServerList(List<String> kafkaServerList) {
        this.kafkaServerList = kafkaServerList;
    }

    public List<String> getKafkaServerList() {
        return kafkaServerList;
    }


    public void setServerPropertiesMap(HashMap<String, Properties> serverPropertiesMap) {
        this.serverPropertiesMap = serverPropertiesMap;
    }

    public HashMap<String, Properties> getServerPropertiesMap() {
        return serverPropertiesMap;
    }

    public void addServerProperties(String serverName, Properties serverProperties) {
        this.serverPropertiesMap.put(serverName, serverProperties);
    }

    public Properties getServerProperties(String serverName) {
        Properties serverProperties = this.serverPropertiesMap.get(serverName);
        return serverProperties;
    }
}

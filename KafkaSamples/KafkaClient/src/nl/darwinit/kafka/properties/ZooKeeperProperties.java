package nl.darwinit.kafka.properties;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.UnknownHostException;

import nl.darwinit.kafka.logging.Log;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

/**
 * Taken from https://www.programcreek.com/java-api-examples/?code=txazo/zookeeper/zookeeper-master/src/main/java/org/apache/zookeeper/server/ZooKeeperServerMain.java#
 * and org.apache.zookeeper.server.quorum.QuorumPeerConfig
 */


public class ZooKeeperProperties {
    private static Log log = new Log(ZooKeeperProperties.class);
    // Default Max Client Connections
    private static int DFT_MAX_CLT_CNXNS = 60;
    // Default min session time out
    private static int DFT_MIN_SESS_TO = -1;
    // Default min session time out
    private static int DFT_MAX_SESS_TO = -1;


    private String dataDir;
    private String dataLogDir;
    private InetSocketAddress clientPortAddress;
    private int maxClientCnxns = DFT_MAX_CLT_CNXNS;
    private int tickTime = ZooKeeperServer.DEFAULT_TICK_TIME;
    private int minSessionTimeout = DFT_MIN_SESS_TO;
    private int maxSessionTimeout = DFT_MAX_SESS_TO;

    public void setProperties(Properties zkProperties) throws UnknownHostException {
        final String methodName = "setProperties(Properties)";
        log.start(methodName);
        setDataDir(zkProperties.getStringValue("dataDir"));
        // Default dataLogDir with dataDir
        setDataLogDir(zkProperties.getStringValue("dataLogDir", "dataDir"));
        log.debug(methodName, "clientPort: " + zkProperties.getIntValue("clientPort"));
        setClientPortAddress(zkProperties.getStringValue("clientPortAddress"), zkProperties.getIntValue("clientPort"));

        log.debug(methodName,
                  "Max Client Connections: " + zkProperties.getIntValue("maxClientCnxns", DFT_MAX_CLT_CNXNS));
        setMaxClientCnxns(zkProperties.getIntValue("maxClientCnxns", DFT_MAX_CLT_CNXNS));
        setTickTime(zkProperties.getIntValue("tickTime", ZooKeeperServer.DEFAULT_TICK_TIME));
        setMinSessionTimeout(zkProperties.getIntValue("minSessionTimeout", DFT_MIN_SESS_TO));
        setMaxSessionTimeout(zkProperties.getIntValue("maxSessionTimeout", DFT_MAX_SESS_TO));
        log.end(methodName);
    }


    public ZooKeeperProperties(Properties zkProperties) throws UnknownHostException {
        super();
        final String methodName = "ZooKeeperProperties(Properties)";
        log.start(methodName);
        setProperties(zkProperties);
        log.end(methodName);
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataLogDir(String dataLogDir) {
        this.dataLogDir = dataLogDir;
    }

    public String getDataLogDir() {
        return dataLogDir;
    }

    public void setMaxClientCnxns(int maxClientCnxns) {
        this.maxClientCnxns = maxClientCnxns;
    }

    public int getMaxClientCnxns() {
        return maxClientCnxns;
    }

    public void setClientPortAddress(InetSocketAddress clientPortAddress) {
        this.clientPortAddress = clientPortAddress;
    }

    public void setClientPortAddress(String clientAddress, int clientPort) throws UnknownHostException {
        if (clientPort == 0) {
            throw new IllegalArgumentException("clientPort is not set");
        }
        if (clientPortAddress != null) {
            this.clientPortAddress = new InetSocketAddress(InetAddress.getByName(clientAddress), clientPort);
        } else {
            this.clientPortAddress = new InetSocketAddress(clientPort);
        }
    }

    public InetSocketAddress getClientPortAddress() {
        return clientPortAddress;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setMinSessionTimeout(int minSessionTimeout) {
        this.minSessionTimeout = minSessionTimeout;
    }

    public int getMinSessionTimeout() {
        return minSessionTimeout;
    }

    public void setMaxSessionTimeout(int maxSessionTimeout) {
        this.maxSessionTimeout = maxSessionTimeout;
    }

    public int getMaxSessionTimeout() {
        return maxSessionTimeout;
    }


}

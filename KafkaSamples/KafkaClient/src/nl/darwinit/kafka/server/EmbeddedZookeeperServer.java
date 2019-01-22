/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 * 
 * EmbeddedZookeeperServer is an Observer of the ZooKeeperDriver class, 
 * to be able to start a ZooKeeper in a seperate thread and have it shutdown based upon an update signal.
 * This class is based on https://www.programcreek.com/java-api-examples/?code=txazo/zookeeper/zookeeper-master/src/main/java/org/apache/zookeeper/server/ZooKeeperServerMain.java.
 * Used to be able to extend and adapt it. 
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.server;

import java.io.File;
import java.io.IOException;

import java.util.Observable;
import java.util.Observer;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.ZooKeeperProperties;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;


/**
 * This class starts and runs a standalone ZooKeeperServer.
 */
public class EmbeddedZookeeperServer implements Observer, Runnable {
    private static final Log log = new Log(EmbeddedZookeeperServer.class);
    private ServerCnxnFactory cnxnFactory;
    private Thread myThread;
    private ZooKeeperProperties zkProperties;
    private ZooKeeperDriver zooKeeperDriver;
    private ZooKeeperServer zooKeeperServer;

    public EmbeddedZookeeperServer() {
        super();
    }

    public EmbeddedZookeeperServer(Observable zooKeeperDriver, ZooKeeperProperties zkProperties) {
        super();
        final String methodName="EmbeddedZookeeperServer(Observable, ZooKeeperProperties)";
        log.start(methodName);
        this.setZkProperties(zkProperties);
        if (zooKeeperDriver instanceof ZooKeeperDriver) {
            log.info(methodName, "Add observer "+this.getClass().getName()+" to observable "+zooKeeperDriver.getClass().getName());
            setZooKeeperDriver((ZooKeeperDriver) zooKeeperDriver);
            zooKeeperDriver.addObserver(this);
        }
        log.end(methodName);
    }

    /**
     * Run from ZooKeeperProperties.
     * @param zkProperties ZooKeeperProperties to use.
     * @throws IOException
     */
    public void runFromProperties(ZooKeeperProperties zkProperties) throws IOException {
        final String methodName = "runFromProperties";
        log.start(methodName);
        log.info(methodName, "Starting server");
        FileTxnSnapLog txnLog = null;
        try {
            // Note that this thread isn't going to be doing anything else,
            // so rather than spawning another thread, we will just call
            // run() in this thread.
            // create a file logger url from the command line args
            ZooKeeperServer zkServer = new ZooKeeperServer();

            txnLog = new FileTxnSnapLog(new File(zkProperties.getDataLogDir()), new File(zkProperties.getDataDir()));
            zkServer.setTxnLogFactory(txnLog);
            zkServer.setTickTime(zkProperties.getTickTime());
            zkServer.setMinSessionTimeout(zkProperties.getMinSessionTimeout());
            zkServer.setMaxSessionTimeout(zkProperties.getMaxSessionTimeout());
            setZooKeeperServer(zkServer);

            cnxnFactory = ServerCnxnFactory.createFactory();
            log.debug(methodName, "Create Server Connection Factory");
            log.debug(methodName, "Server Tick Time: " + zkServer.getTickTime());
            log.debug(methodName, "ClientPortAddress: " + zkProperties.getClientPortAddress());
            log.debug(methodName, "Max Client Connections: " + zkProperties.getMaxClientCnxns());
            cnxnFactory.configure(zkProperties.getClientPortAddress(), zkProperties.getMaxClientCnxns());
            log.debug(methodName, "Startup Server Connection Factory");
            cnxnFactory.startup(zkServer);
            cnxnFactory.join();
            if (zkServer.isRunning()) {
                zkServer.shutdown();
            }
        } catch (InterruptedException e) {
            // warn, but generally this is ok
            log.warn(methodName, "Server interrupted", e);
        } finally {
            if (txnLog != null) {
                txnLog.close();
            }
        }
        log.end(methodName);
    }

    /**
     * Shutdown the serving instance
     */
    public void shutdown() {
        final String methodName = "shutdown";
        log.start(methodName);
        log.info(methodName,"Let me shutdown "+myThread.getName());
        ZooKeeperServer zkServer = getZooKeeperServer();
        ServerCnxnFactory cnxnFactory = getCnxnFactory();
        cnxnFactory.shutdown();
        if (zkServer.isRunning()) {
            zkServer.shutdown();
        }
        log.end(methodName);
    }

    @Override
    public void update(Observable o, Object arg) {
        final String methodName = "update(Observable,Object)";
        log.start(methodName);
        log.info(methodName, getMyThread().getName() + " - Got status update from Observable!");
        ZooKeeperDriver zkDriver = getZooKeeperDriver();
        if (zkDriver.isShutdownZookeepers()) {
            log.info(methodName, getMyThread().getName() + " - Apparently I´ve got to shutdown myself!");
            shutdown();
        } else {
            log.info(methodName, getMyThread().getName() + " - Don't know what to do with this status update!");
        }
        log.end(methodName);
    }

    @Override
    public void run() {
        final String methodName = "run";
        log.start(methodName);
        try {
            runFromProperties(getZkProperties());
        } catch (IOException ioe) {
            log.error(methodName, "Run failed!", ioe);
        }
        log.end(methodName);

    }

    public void setCnxnFactory(ServerCnxnFactory cnxnFactory) {
        this.cnxnFactory = cnxnFactory;
    }

    public ServerCnxnFactory getCnxnFactory() {
        return cnxnFactory;
    }

    public void setZkProperties(ZooKeeperProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    public ZooKeeperProperties getZkProperties() {
        return zkProperties;
    }


    public void setZooKeeperDriver(ZooKeeperDriver zooKeeperDriver) {
        this.zooKeeperDriver = zooKeeperDriver;
    }

    public ZooKeeperDriver getZooKeeperDriver() {
        return zooKeeperDriver;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public void setZooKeeperServer(ZooKeeperServer zooKeeperServer) {
        this.zooKeeperServer = zooKeeperServer;
    }

    public ZooKeeperServer getZooKeeperServer() {
        return zooKeeperServer;
    }
}

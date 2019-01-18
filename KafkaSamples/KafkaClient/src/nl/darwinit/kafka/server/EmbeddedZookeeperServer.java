/**
 * This class is taken from https://www.programcreek.com/java-api-examples/?code=txazo/zookeeper/zookeeper-master/src/main/java/org/apache/zookeeper/server/ZooKeeperServerMain.java.
 * Used to be able to extend and adapt it. 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.darwinit.kafka.server;

import java.io.File;
import java.io.IOException;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.properties.ZooKeeperProperties;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;


/**
 * This class starts and runs a standalone ZooKeeperServer.
 */
public class EmbeddedZookeeperServer {
    private static final Log log = new Log(EmbeddedZookeeperServer.class);


    private ServerCnxnFactory cnxnFactory;

    /*
     * Start up the ZooKeeper server.
     *
     * @param args the configfile or the port datadir [ticktime]
     */
    public static void main(String[] args) {
        final String methodName = "main";
        log.start(methodName);
        log.info(methodName, "Exiting normally");
        log.end(methodName);
        System.exit(0);
    }


    /**
     * Run from a ServerConfig.
     * @param config ServerConfig to use.
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
            
            cnxnFactory = ServerCnxnFactory.createFactory();
            log.debug(methodName, "Create Server Connection Factory");
            log.debug(methodName, "Server Tick Time: "+zkServer.getTickTime());
            log.debug(methodName, "ClientPortAddress: "+zkProperties.getClientPortAddress());
            log.debug(methodName, "Max Client Connections: "+zkProperties.getMaxClientCnxns());
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
        final String methodName = "runFromProperties";
        log.start(methodName);
        cnxnFactory.shutdown();
        log.end(methodName);
    }
}

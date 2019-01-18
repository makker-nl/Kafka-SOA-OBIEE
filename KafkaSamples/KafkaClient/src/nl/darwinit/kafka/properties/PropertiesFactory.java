package nl.darwinit.kafka.properties;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import nl.darwinit.kafka.logging.Log;
import nl.darwinit.kafka.server.ZooKeeperStarter;


public abstract class PropertiesFactory {
    private static Log log = new Log(PropertiesFactory.class); 
    public static final String ZK_PROPS = "zookeeper.properties";
    public static final String DFT_KS_PROPS = "server.properties";

    /**
     * GetProperties to load a property file from classpath
     * From https://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
     * @param propertyFileName
     * @return
     * @throws IOException
     */
    public static Properties getProperties(String propertyFileName) throws IOException {
        final String methodName = "Properties";
        log.start(methodName);
        Properties properties = new Properties();

        InputStream inputStream = PropertiesFactory.class.getClassLoader().getResourceAsStream(propertyFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propertyFileName + "' not found in the classpath");
        }
        log.end(methodName);
        return properties;
    }

    /**
     * Get Zookeeper properties
     * @return
     * @throws IOException
     */
    public static ZooKeeperProperties getZKProperties() throws IOException {
        final String methodName = "ZooKeeperProperties";
        log.start(methodName);
        Properties properties = getProperties(ZK_PROPS);
        ZooKeeperProperties zkProperties = new ZooKeeperProperties(properties);
        log.end(methodName);
        return zkProperties;
    }

    /**
     * Get Default Kafka Server properties
     * @return
     * @throws IOException
     */
    public static Properties getKSProperties() throws IOException {
        final String methodName = "ZooKeeperProperties";
        log.start(methodName);        
        Properties properties = getProperties(DFT_KS_PROPS);
        log.end(methodName);
        return properties;
    }

}

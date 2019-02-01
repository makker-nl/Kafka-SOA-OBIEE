/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 * 
 * Factory to load property files from class path in a common way.
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.darwinit.kafka.logging.Log;


public abstract class PropertiesFactory {
    private static Log log = new Log(PropertiesFactory.class);
    public static final String ZK_PROPS = "zookeeper.properties";
    public static final String DFT_KS_PROPS = "server.properties";
    public static final String KSD_PROPS = "kafkaserverdriver.properties";

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
        final String methodName = "getZKProperties";
        log.start(methodName);
        Properties properties = getProperties(ZK_PROPS);
        ZooKeeperProperties zkProperties = new ZooKeeperProperties(properties);
        log.end(methodName);
        return zkProperties;
    }

     /**
     * Get Default Kafka Server properties based on ServerName
     * @param propFileName
     * @return
     * @throws IOException
     */
    public static Properties getKSProperties(String propFileName) throws IOException {
        final String methodName = "getKSProperties";
        log.start(methodName);
        Properties ksProperties = getProperties(propFileName);        
        log.end(methodName);
        return ksProperties;
    }
    
    /**
     * Get Default Kafka Server properties
     * @return
     * @throws IOException
     */
    public static Properties getKSProperties() throws IOException {
        final String methodName = "getKSProperties()";
        log.start(methodName);
        Properties ksProperties = getKSProperties(DFT_KS_PROPS);        
        log.end(methodName);
        return ksProperties;
    }
    
    /**
     * Get Default Kafka Cluster properties
     * @return
     * @throws IOException
     */
    public static KafkaServerDriverProperties getKSDProperties() throws IOException {
        final String methodName = "getKSDProperties";
        log.start(methodName);
        Properties properties = getProperties(KSD_PROPS);
        KafkaServerDriverProperties ksdProperties = new KafkaServerDriverProperties(properties);
        log.end(methodName);
        return ksdProperties;
    }
    
        
}

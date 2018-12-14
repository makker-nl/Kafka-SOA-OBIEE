package nl.darwinit.kafka.properties;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public abstract class PropertiesFactory {
    public static final String ZK_PROPS="zookeeper.properties";
    public static final String DFT_KS_PROPS="server.properties";
    
    /**
     * GetProperties to load a property file from classpath
     * From https://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
     * @param propertyFileName
     * @return
     * @throws IOException
     */
    public static Properties getProperties(String propertyFileName) throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = PropertiesFactory.class.getClassLoader().getResourceAsStream(propertyFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propertyFileName + "' not found in the classpath");
        }
        return properties;
    }
    /**
     * Get Zookeeper properties
     * @return
     * @throws IOException
     */
    public static Properties getZKProperties() throws IOException {
        return getProperties(ZK_PROPS);
    }
    /**
     * Get Default Kafka Server properties
     * @return
     * @throws IOException
     */
    public static Properties getKSProperties() throws IOException {
        return getProperties(DFT_KS_PROPS);
    }
    
}

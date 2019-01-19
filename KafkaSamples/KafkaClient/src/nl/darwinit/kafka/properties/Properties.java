/**
 * @author Martien van den Akker, Darwin-IT Professionals
 * @version 1.0
 *
 * Wrapper class around java.util.Properties to be able top get typed and/or defaulted properties.
 * 
 * History
 * 2019-01-19 - 1.0 - Initial Creation
 */
package nl.darwinit.kafka.properties;

public class Properties extends java.util.Properties {
    @SuppressWarnings("compatibility:-267375295948078196")
    private static final long serialVersionUID = 1162732492869841368L;

    public Properties(java.util.Properties properties) {
        super(properties);
    }

    public Properties() {
        super();
    }

    public String getStringValue(String name) {
        String propertyValue = getProperty(name);
        if (propertyValue != null) {
            propertyValue = propertyValue.toString().trim();
        }
        return propertyValue;
    }
    
    public String getStringValue(String name, String defaultName) {
        String propertyValue = getStringValue(name);
        if (propertyValue == null) {
            propertyValue = getStringValue(defaultName);
        }
        return propertyValue;
    }

    public int getIntValue(String name, int defaultValue) {
        String value = getStringValue(name);
        int intValue = defaultValue;
        if (value != null) {
            intValue = Integer.parseInt(value);
        }
        return intValue;
    }

    public int getIntValue(String name) {      
        return getIntValue(name, 0) ;
    }
}

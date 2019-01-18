package nl.darwinit.kafka.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private Logger log;
    private String className;
    private boolean toConsole = true;

    /**
     * Only log to console if toConsole is true, unless override is true
     * @param logText
     * @param override
     */
    private void pl(String logText, boolean override) {
        if (toConsole || !override) {
            System.out.println(logText);
        }
    }

    /**
     * Only log to console if toConsole is true;
     * @param logText
     */
    private void pl(String logText) {
        pl(logText, false);
    }

    public Log(Class<?> loggingClass) {
        super();
        log = LoggerFactory.getLogger(loggingClass);
        className = loggingClass.getName();
    }

    private String getText(String methodName, String text) {
        return className + "." + methodName + ": " + text;
    }

    public void debug(String methodName, String text) {
        String logText = getText(methodName, text);
        pl(logText);
        log.debug(logText);
    }

    public void info(String methodName, String text) {
        String logText = getText(methodName, text);
        pl(logText);
        log.info(logText);
    }

    public void warn(String methodName, String text) {
        String logText = getText(methodName, text);
        pl(logText);
        log.warn(logText);
    }

    public void warn(String methodName, String text, Exception e) {
        String logText = getText(methodName, text);
        pl(logText, true);
        pl("Error Message: " + e.getMessage());
        pl("Error Caused by: " + e.getCause());
        log.warn(logText, e);
    }

    public void error(String methodName, String text) {
        String logText = getText(methodName, text);
        pl(logText, true);
        log.error(logText);
    }


    public void error(String methodName, String text, Exception e) {
        String logText = getText(methodName, text);
        pl(logText, true);
        pl("Error Message: " + e.getMessage());
        pl("Error Caused by: " + e.getCause());
        log.error(logText, e);
    }

    public void start(String methodName) {
        debug(methodName, "Start");
    }

    public void end(String methodName) {
        debug(methodName, "End");
    }
}

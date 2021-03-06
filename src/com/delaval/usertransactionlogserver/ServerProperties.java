package com.delaval.usertransactionlogserver;

import com.delaval.usertransactionlogserver.persistence.dao.InitDAO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton.
 * Holds all the properties needed in the UserTransactionLogServer.
 */
public class ServerProperties {

    private static ServerProperties _instance;

    private static final String PROP_FILE_NAME = "UserTransactionLogServer.properties";

    public enum PropKey {
        DB_SERVER_HOST("dbServerHost"),
        DB_SERVER_PORT("dbServerPort"),
        DB_NAME("dbName"),
        DB_USER("dbUser"),
        DB_PWD("dbPassword"),
        JMS_CONNECTION("jmsConnection"),
        JMS_QUEUE_DEST_CLICK("jmsQueueClickLog"),
        JMS_QUEUE_DEST_EVENT("jmsQueueEventLog"),
        THREAD_POOL_SIZE("threadPoolSize"),
        LOGGING_KEY("loggingKey"),
        DELETE_LOGS_EVENT_START("eventStartDay"),
        DELETE_LOGS_INTERVAL_DEFAULT_IN_DAYS("deleteLogsIntervalDefault"),
        SYSTEM_PROPERTY_NAME_DELETE_CLICK_LOGS_INTERVAL("deleteClickLogsIntervalName"),
        SYSTEM_PROPERTY_NAME_DELETE_EVENT_LOGS_INTERVAL("deleteEventLogsIntervalName"),
        WEBSOCKET_PORT("websocketPort");

        private String myValue;

        PropKey(String value){
            myValue = value;
        }

        public String getMyValue(){
            return myValue;
        }
    }

    private Properties prop = new Properties();

    private ServerProperties(){
        setProperties();
    }

    public static ServerProperties getInstance(){
        if (_instance == null) {
            synchronized (ServerProperties.class) {
                if (_instance == null) {
                    _instance = new ServerProperties();
                }
            }
        }
        return _instance;
    }

    private String get(PropKey propType){
        return prop.getProperty(propType.getMyValue());
    }

    /**
     * Get serverproperty with a certain key
     * @param propkey
     * @return the value of the property with this key
     */
    public String getProp(PropKey propkey){
        return get(propkey);
    }

    private Properties setProperties() {
        try {
            prop.load(new FileInputStream(PROP_FILE_NAME));
        } catch (IOException ex) {
            try {
                // Create defa  ult values
                set(PropKey.DB_SERVER_HOST, "localhost");
                set(PropKey.DB_SERVER_PORT, "3306");
                set(PropKey.DB_NAME, "user_transaction_log_server");
//                set(PropKey.DB_NAME, "test");
                set(PropKey.DB_USER, "logAdmin");
                set(PropKey.DB_PWD, "admin");
                set(PropKey.THREAD_POOL_SIZE, "500");
//                set(PropKey.JMS_CONNECTION, "failover:tcp://localhost:61616");
//                set(PropKey.JMS_CONNECTION, "failover:(tcp://localhost:61616)?startupMaxReconnectAttempts=1");
                set(PropKey.JMS_CONNECTION, "failover:(tcp://10.34.35.1:61616)?startupMaxReconnectAttempts=1");
//                set(PropKey.JMS_CONNECTION, "tcp://10.34.35.1:61616");
                set(PropKey.JMS_QUEUE_DEST_CLICK, "UserTransactionClickLog");
                set(PropKey.JMS_QUEUE_DEST_EVENT, "UserTransactionEventLog");
                set(PropKey.WEBSOCKET_PORT, "8085");
                set(PropKey.LOGGING_KEY, "utlserver");
                set(PropKey.DELETE_LOGS_EVENT_START, "'2016-01-13 10:55:00'");
                set(PropKey.DELETE_LOGS_INTERVAL_DEFAULT_IN_DAYS, InitDAO.DEFAULT_DELETE_INTERVAL_IN_DAYS);
                set(PropKey.SYSTEM_PROPERTY_NAME_DELETE_CLICK_LOGS_INTERVAL, "deleteClickLogsIntervalName");
                set(PropKey.SYSTEM_PROPERTY_NAME_DELETE_EVENT_LOGS_INTERVAL, "deleteEventLogsIntervalName");
                prop.store(new FileOutputStream(PROP_FILE_NAME), null);
            } catch (IOException e) {
                System.out.println("Got problem creating " + PROP_FILE_NAME + " file! ");
            }
        }
        return prop;
    }

    void set(PropKey propType, String propValue){
        prop.setProperty(propType
                .getMyValue(), propValue);
    }
}

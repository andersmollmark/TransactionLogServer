package com.delaval.usertransactionlogserver.util;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by delaval on 2016-02-04.
 */
public class UtlsLogUtil {

    public static Map<Session, Date> sessions = new HashMap<>();
    public static Map<String, List<Session>> sessionsPerHost = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger("utlserver");

    public static void warn(Class clazz, String mess) {
        LOGGER.warn(clazz.getSimpleName() + ": " + mess);
    }

    public static void warn(Class clazz, Exception e) {
        warn(clazz, e.getMessage());
    }

    public static void info(Class clazz, String mess) {
        LOGGER.info(clazz.getSimpleName() + ": " + mess);
    }

    public static void info(Class clazz, Exception e) {
        info(clazz, e.getMessage());
    }

    public static void error(Class clazz, String mess) {
        LOGGER.error(clazz.getSimpleName() + ": " + mess);
    }

    public static void error(Class clazz, Exception e) {
        error(clazz, e.getMessage());
    }

    public static void debug(Class clazz, String mess) {
        LOGGER.debug(clazz.getSimpleName() + ": " + mess);
    }

    public static void debug(Class clazz, Exception e) {
        debug(clazz, e.getMessage());
    }

}

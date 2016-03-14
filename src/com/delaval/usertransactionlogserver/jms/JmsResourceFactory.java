package com.delaval.usertransactionlogserver.jms;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * Singleton.
 * Makes it possible to get jms-templates in an easy way.
 */
public class JmsResourceFactory {

    AnnotationConfigApplicationContext ctx;

    private static JmsResourceFactory _clickLogInstance;
    private static JmsResourceFactory _eventLogInstance;

    private JmsResourceFactory(){
        // empty by default
    }

    public static JmsResourceFactory getClickLogInstance() {
        if (_clickLogInstance == null) {
            synchronized (JmsResourceFactory.class) {
                if (_clickLogInstance == null) {
                    _clickLogInstance = new JmsResourceFactory();
                    _clickLogInstance.ctx = new AnnotationConfigApplicationContext(AppConfigClickLog.class);
                }
            }
        }
        return _clickLogInstance;
    }

    public static JmsResourceFactory getEventLogInstance() {
        if (_eventLogInstance == null) {
            synchronized (JmsResourceFactory.class) {
                if (_eventLogInstance == null) {
                    _eventLogInstance = new JmsResourceFactory();
                    _eventLogInstance.ctx = new AnnotationConfigApplicationContext(AppConfigEventLog.class);
                }
            }
        }
        return _eventLogInstance;
    }

    public static void initApplicationContext(){
        getClickLogInstance();
        getEventLogInstance();
    }

    public AnnotationConfigApplicationContext getCtx(){
        return ctx;
    }

    public JmsTemplate getJmsTemplate(){
        return ctx.getBean(JmsTemplate.class);
    }

    public void closeContext(){
        ctx.close();
    }

}

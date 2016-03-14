/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.delaval.usertransactionlogserver;

import ch.qos.logback.classic.Level;
import com.delaval.usertransactionlogserver.jms.JmsResourceFactory;
import com.delaval.usertransactionlogserver.persistence.ConnectionFactory;
import com.delaval.usertransactionlogserver.persistence.dao.InitDAO;
import com.delaval.usertransactionlogserver.servlet.*;
import com.delaval.usertransactionlogserver.util.UtlsLogUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import simpleorm.utils.SLogSlf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * that initializes the Jetty-server.
 */
public class UserTransactionLogServer {

    private Server server;

    private UserTransactionLogServer() {
        // empty by design
    }

    public void init() {
        try {
            if (isCreateTables()) {
                InitDAO.getInstance().createTables();
            }
            InitDAO.getInstance().createDeleteLogEvent();
        } catch (Exception e) {
            UtlsLogUtil.error(this.getClass(), "FATAL ERROR, something went wrong while creating tables in db:" + e.getMessage());
            ConnectionFactory.closeFactory();
            System.exit(0);
        }
        initServer();
    }

    private void initServer() {
        QueuedThreadPool threadPool = new QueuedThreadPool(); // TODO is this necessary??
        int threadPoolSize = Integer.parseInt(ServerProperties.getInstance().getProp(ServerProperties.PropKey.THREAD_POOL_SIZE));
        threadPool.setMaxThreads(threadPoolSize);

        int websocketPort = Integer.parseInt(ServerProperties.getInstance().getProp(ServerProperties.PropKey.WEBSOCKET_PORT));
        server = new Server(websocketPort);

        initLogLevel();
        server.setHandler(createServletContextHandler());

        try {
            server.start();
            JmsResourceFactory.initApplicationContext();
            server.join();

        } catch (Exception e) {
            UtlsLogUtil.error(this.getClass(), "FATAL ERROR, something happened while starting server, shutting down:" + e.getMessage());
            ConnectionFactory.closeFactory();
            System.exit(0);
        }

    }

    private ServletContextHandler createServletContextHandler() {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("app");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        context.setInitParameter("cacheControl", "no-cache");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.addServlet(DefaultServlet.class, "/");
        context.addServlet(StartServlet.class, "/servlet");
        context.addServlet(CreateTestLogServlet.class, "/servlet/testServlet");
        context.addServlet(SaveTestLogServlet.class, "/servlet/saveTestLog");
        context.addServlet(GetUserTransactionKeyServlet.class, "/servlet/getUserTransactionKey");
        context.addServlet(GetLogContentServlet.class, "/servlet/getLogContent");
        context.addServlet(ChangeDeleteLogEventServlet.class, "/servlet/changeDeleteLogEvent");


        ServletHolder ws = context.addServlet(LogWebSocketServlet.class, "/ws");
        ws.setInitParameter("classpath", context.getClassPath());
        return context;
    }

    private void initLogLevel(){
        // TODO handle this in groovy as the rest of the servers?
        final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("org.eclipse.jetty");
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
            logbackLogger.setLevel(Level.INFO);
        }
        else{
            throw new IllegalStateException("Logger-class was not instance of logback-Logger");
        }
        // set log-level to SimpleORM, > 40 all transactions will endup in error_log
        SLogSlf4j.getSessionlessLogger().setLevel(0);

    }

    private boolean isCreateTables() {
        try {
            Connection connection = ConnectionFactory.getInstance().getConnection();
            InitDAO initDAO = InitDAO.getInstance();
            ResultSet resultSet = initDAO.getUserTransactionKeyTable(connection);
            if (resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            return true;
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new UserTransactionLogServer().init();
    }

}

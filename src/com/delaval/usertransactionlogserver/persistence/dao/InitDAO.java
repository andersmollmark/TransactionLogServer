package com.delaval.usertransactionlogserver.persistence.dao;

import com.delaval.usertransactionlogserver.ServerProperties;
import com.delaval.usertransactionlogserver.domain.InternalSystemProperty;
import com.delaval.usertransactionlogserver.persistence.ConnectionFactory;
import com.delaval.usertransactionlogserver.persistence.entity.ClickLog;
import com.delaval.usertransactionlogserver.persistence.entity.EventLog;
import com.delaval.usertransactionlogserver.persistence.entity.SystemProperty;
import com.delaval.usertransactionlogserver.persistence.entity.UserTransactionKey;
import com.delaval.usertransactionlogserver.persistence.operation.CreateSystemPropertyOperation;
import com.delaval.usertransactionlogserver.persistence.operation.OperationFactory;
import com.delaval.usertransactionlogserver.persistence.operation.OperationParam;
import com.delaval.usertransactionlogserver.util.UtlsLogUtil;
import simpleorm.sessionjdbc.SSessionJdbc;

import java.sql.*;
import java.util.Date;

/**
 * Creates all the needed tables in DB.
 */
public class InitDAO {

    private static InitDAO _instance;
    public static final String DEFAULT_DELETE_INTERVAL_IN_DAYS = "15";

    private InitDAO(){
        // Empty by design
    }

    public static InitDAO getInstance(){
        if (_instance == null) {
            synchronized (InitDAO.class) {
                if (_instance == null) {
                    _instance = new InitDAO();
                }
            }
        }
        return _instance;
    }

    /**
     * Fetches the UserTransactionKey-table
     * @param connection is the db-connection
     * @return the resultset with the table
     */
    public ResultSet getUserTransactionKeyTable(Connection connection){
        DatabaseMetaData metadata = null;
        ResultSet resultSet = null;
        try {
            metadata = connection.getMetaData();
            resultSet = metadata.getTables(null, null, UserTransactionKey.USER_TRANSACTION_KEY.getTableName(), null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * Creates the database-tables needed.
     */
    public void createTables() throws Exception {
        SSessionJdbc session = null;
        try {
            session = ConnectionFactory.getInstance().getSession(ConnectionFactory.LOG_SERVER_CONN_NAME);
            dropAllTables(session);
            createTables(session);
        } finally {
            if (session != null) {
                session.close();
                ConnectionFactory.getInstance().closeConnection();
            }
        }
    }

    private void dropAllTables(SSessionJdbc ses) {
        ses.begin();
        dropTableNoError(ses, UserTransactionKey.USER_TRANSACTION_KEY.getTableName());
        dropTableNoError(ses, ClickLog.CLICK_LOG.getTableName());
        dropTableNoError(ses, EventLog.EVENT_LOG.getTableName());
        dropTableNoError(ses, SystemProperty.SYSTEM_PROPERTY.getTableName());
        ses.commit();
    }

    private void dropTableNoError(SSessionJdbc ses, String table) {
        ses.flush();
        ses.getDriver().dropTableNoError(table);
        ses.commit();
        ses.begin();
    }

    private void createTables(SSessionJdbc ses) {
        ses.begin();
        ses.rawUpdateDB(ses.getDriver().createTableSQL(UserTransactionKey.USER_TRANSACTION_KEY));
        ses.rawUpdateDB(ses.getDriver().createTableSQL(ClickLog.CLICK_LOG));
        ses.rawUpdateDB(ses.getDriver().createTableSQL(EventLog.EVENT_LOG));
        ses.rawUpdateDB(ses.getDriver().createTableSQL(SystemProperty.SYSTEM_PROPERTY));
        ses.commit();
    }



    public void createDeleteLogEvent() {
        StringBuilder sql = new StringBuilder("SET GLOBAL event_scheduler = ON");
        String errorMessSchedule = "Something went wrong when creating delete_log-event:";
        runSqlCommand(sql, errorMessSchedule);
        createDeleteClickLogEvent();
        createDeleteEventLogEvent();
    }

    public void createDeleteClickLogEvent() {

        String deleteClickLogsIntervalName = ServerProperties.getInstance().getProp(ServerProperties.PropKey.SYSTEM_PROPERTY_NAME_DELETE_CLICK_LOGS_INTERVAL);
        String deleteInterval = getDeleteInterval(deleteClickLogsIntervalName);

        StringBuilder sqlEvent = new StringBuilder();
        sqlEvent.append("CREATE EVENT IF NOT EXISTS ")
                .append("delete_click_log ")
                .append("ON SCHEDULE EVERY 1 DAY STARTS ")
                .append(ServerProperties.getInstance().getProp(ServerProperties.PropKey.DELETE_LOGS_EVENT_START))
                .append(" ON COMPLETION PRESERVE ")
                .append("DO DELETE FROM ClickLog WHERE timestamp < DATE_SUB(NOW(), INTERVAL " + deleteInterval + " DAY)");
        String errorMess = "Something went wrong when creating delete_click_log-event:";
        runSqlCommand(sqlEvent, errorMess);
    }

    public void createDeleteEventLogEvent() {

        String deleteEventLogsIntervalName = ServerProperties.getInstance().getProp(ServerProperties.PropKey.SYSTEM_PROPERTY_NAME_DELETE_EVENT_LOGS_INTERVAL);
        String deleteInterval = getDeleteInterval(deleteEventLogsIntervalName);

        StringBuilder sqlEvent = new StringBuilder();
        sqlEvent.append("CREATE EVENT IF NOT EXISTS ")
                .append("delete_event_logs ")
                .append("ON SCHEDULE EVERY 1 DAY STARTS ")
                .append(ServerProperties.getInstance().getProp(ServerProperties.PropKey.DELETE_LOGS_EVENT_START))
                .append(" ON COMPLETION PRESERVE ")
                .append("DO DELETE FROM EventLog WHERE timestamp < DATE_SUB(NOW(), INTERVAL " + deleteInterval + " DAY)");
        String errorMess = "Something went wrong when creating delete_event_log-event:";
        runSqlCommand(sqlEvent, errorMess);
    }

    private String getDeleteInterval(String name){
        String sql = "SELECT * FROM SystemProperty WHERE NAME='" + name + "'";
        Connection connection = ConnectionFactory.getInstance().getConnection();
        ResultSet resultSet = null;
        String interval = ServerProperties.getInstance().getProp(ServerProperties.PropKey.DELETE_LOGS_INTERVAL_DEFAULT_IN_DAYS);
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                interval = resultSet.getString(SystemProperty.VALUE_COLUMN);
            }
            else{
                try {
                    tryClose(resultSet);
                    createDeleteIntervalProperty(name, interval);
                } catch (SQLException e) {
                    UtlsLogUtil.error(this.getClass(), "Something went wrong when closing resultset:" + e.getMessage());
                }
            }

        } catch (SQLException e) {
            UtlsLogUtil.error(this.getClass(), "Something went wrong while fetching deleteinterval with name:" + name + ", exception:" + e.getMessage());
        }
        finally {
            try {
                tryClose(resultSet);
            } catch (SQLException e) {
                UtlsLogUtil.error(this.getClass(), "Something went wrong when closing resultset:" + e.getMessage());
            }
        }
        return interval;
    }

    private void tryClose(ResultSet resultSet) throws SQLException {
        if(resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
    }

    private void createDeleteIntervalProperty(String name, String interval) {
        if(interval == null){
            UtlsLogUtil.error(this.getClass(), "Missing serverproperty: " + name + ", setting " + DEFAULT_DELETE_INTERVAL_IN_DAYS + " as default");
        }
        InternalSystemProperty newProperty = new InternalSystemProperty();
        newProperty.setName(name);
        newProperty.setValue(interval != null ? interval : DEFAULT_DELETE_INTERVAL_IN_DAYS);
        newProperty.setTimestamp(new Date());
        OperationParam<CreateSystemPropertyOperation> createSystemPropertyParamForSystem = OperationFactory.getCreateSystemPropertyParamForSystem(newProperty);
        OperationDAO.getInstance().executeOperation(createSystemPropertyParamForSystem);
    }


    private void runSqlCommand(StringBuilder sql, String errorMess){
        Connection connection = ConnectionFactory.getInstance().getConnection();
        ResultSet resultSet = null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            resultSet = ps.executeQuery();

        } catch (SQLException e) {
            UtlsLogUtil.error(this.getClass(), errorMess + e.getMessage());
        }
        finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                UtlsLogUtil.error(this.getClass(), "Something went wrong when closing resultset:" + e.getMessage());
            }
        }
    }

}

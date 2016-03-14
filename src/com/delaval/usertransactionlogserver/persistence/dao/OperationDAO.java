package com.delaval.usertransactionlogserver.persistence.dao;

import com.delaval.usertransactionlogserver.persistence.ConnectionFactory;
import com.delaval.usertransactionlogserver.persistence.operation.Operation;
import com.delaval.usertransactionlogserver.persistence.operation.OperationFactory;
import com.delaval.usertransactionlogserver.persistence.operation.OperationParam;
import com.delaval.usertransactionlogserver.util.UtlsLogUtil;
import simpleorm.sessionjdbc.SSessionJdbc;

/**
 * Singleton.
 * Handles the persistence-logic of the tables.
 */
public class OperationDAO {

    private static final String LOG_SERVER_CONN_NAME = "LogServer";

    private static OperationDAO _instance;

    private OperationDAO() {
        // Empty by design
    }

    public static OperationDAO getInstance() {
        if (_instance == null) {
            synchronized (OperationDAO.class) {
                if (_instance == null) {
                    _instance = new OperationDAO();
                }
            }
        }
        return _instance;
    }

    public synchronized <T extends Operation> T executeOperation(OperationParam<T> operationParam) {
        SSessionJdbc ses = ConnectionFactory.getInstance().getSession(LOG_SERVER_CONN_NAME);
        ses.getStatistics();

        Class<T> operationClass = operationParam.getOperationClass();
        Operation operation = null;

        try {
            operation = doExecute(operationParam, ses);
        } catch (Exception e) {
            String error = operationParam.getOperationClass().getName() + " " +
                    (operationParam.isCreateUpdate() ? operationParam.getWebSocketMessage().toString() : " parameter:" + operationParam.getParameter());
            UtlsLogUtil.error(this.getClass(), "Something went wrong while executing the operation:" + e.getMessage() + "\n" + error);
            ses.rollback();
        } finally {
            ses.close();
        }
        return  operationClass.cast(operation);
    }

    private Operation doExecute(OperationParam operationParam, SSessionJdbc ses) throws InstantiationException, IllegalAccessException {
        Operation operation = null;
        if(operationParam.isCreateUpdate()){
            operation = OperationFactory.getCreateUpdateOperation(ses, operationParam);
        }
        else{
            operation = OperationFactory.getReadOperation(ses, operationParam);
        }
        operation.validate();
        try {
            ses.begin();
            operation.execute();
        }
        finally {
            ses.flush();
            ses.commit();
        }
        return operation;
    }



}

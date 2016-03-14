package com.delaval.usertransactionlogserver.persistence.operation;

import com.delaval.usertransactionlogserver.domain.InternalUserTransactionKey;
import com.delaval.usertransactionlogserver.persistence.ConnectionFactory;
import com.delaval.usertransactionlogserver.persistence.entity.UserTransactionKey;
import com.delaval.usertransactionlogserver.util.UtlsLogUtil;
import simpleorm.dataset.SQuery;
import simpleorm.dataset.SQueryResult;
import simpleorm.sessionjdbc.SSessionJdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns all UserTransactionKey that exist in db
 */
public class GetAllUserTransactionKeysOperation implements ReadOperation {

    private SSessionJdbc jdbcSession;
    private List<InternalUserTransactionKey> operationResult;

    @Override
    public void setJdbcSession(SSessionJdbc session) {
        jdbcSession = session;
    }

    @Override
    public void validate() {
        if (jdbcSession == null) {
            throw new IllegalStateException("The operation-instance cant have null as a jdbc-session");
        }
    }

    @Override
    public void execute() {
        UtlsLogUtil.debug(this.getClass(), "Get all userTransactionKeys");
        final List<InternalUserTransactionKey> all = new ArrayList<>();
        SQueryResult<UserTransactionKey> result = jdbcSession.query(new SQuery(UserTransactionKey.USER_TRANSACTION_KEY));
        List<UserTransactionKey> resultList = new ArrayList<>();
        resultList.addAll(result);
        resultList.forEach(context -> all.add(new com.delaval.usertransactionlogserver.domain.InternalUserTransactionKey(context)));
        operationResult = all;


    }

    @Override
    public void setReadParameter(String parameter) {
        // does nothing
    }

    @Override
    public List<InternalUserTransactionKey> getResult() {
        return operationResult;
    }
}

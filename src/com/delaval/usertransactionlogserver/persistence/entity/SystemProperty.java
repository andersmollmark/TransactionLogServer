package com.delaval.usertransactionlogserver.persistence.entity;

import com.delaval.usertransactionlogserver.util.DateUtil;
import com.delaval.usertransactionlogserver.websocket.ClickLogContent;
import com.delaval.usertransactionlogserver.websocket.WebSocketMessage;
import com.google.gson.Gson;
import simpleorm.dataset.SFieldString;
import simpleorm.dataset.SFieldTimestamp;
import simpleorm.dataset.SRecordMeta;
import simpleorm.sessionjdbc.SSessionJdbc;

import java.util.Date;
import java.util.Optional;

import static simpleorm.dataset.SFieldFlags.SDESCRIPTIVE;
import static simpleorm.dataset.SFieldFlags.SPRIMARY_KEY;

/**
 * Entity that mirrors ClickLog-table
 */
public class SystemProperty extends AbstractEntity {

    public static final SRecordMeta SYSTEM_PROPERTY = new SRecordMeta(SystemProperty.class, "SystemProperty");
    public static final SFieldString ID = new SFieldString(SYSTEM_PROPERTY, "id", 100, SPRIMARY_KEY);
    public static final SFieldString NAME = new SFieldString(SYSTEM_PROPERTY, "name", 30, SDESCRIPTIVE);
    public static final SFieldString VALUE = new SFieldString(SYSTEM_PROPERTY, "value", 100, SDESCRIPTIVE);
    public static final SFieldString USER_TRANSACTION_KEY_ID = new SFieldString(SYSTEM_PROPERTY, "userTransactionKeyId", 100, SDESCRIPTIVE);
    public static final SFieldTimestamp TIMESTAMP = new SFieldTimestamp(SYSTEM_PROPERTY, "timestamp").overrideSqlDataType("TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

    public static final String VALUE_COLUMN = "value";
    public static final String SYSTEM_USER = "System";

    public String getId(){
        return getString(ID);
    }

    public String getName(){
        return getString(NAME);
    }

    public String getValue(){
        return getString(VALUE);
    }

    public Date getTimestamp(){
        Date date = new Date();
        date.setTime(getTimestamp(TIMESTAMP).getTime());
        return date;
    }


    @Override
    public SRecordMeta<SystemProperty> getMeta() {
        return SYSTEM_PROPERTY;
    }

    @Override
    public void createUserTransactionId(WebSocketMessage webSocketMessage) {
        setString(SystemProperty.USER_TRANSACTION_KEY_ID, getUserTransactionKeyId(webSocketMessage));
    }

    public static Optional<SystemProperty> find(SSessionJdbc ses, String id) {
        SystemProperty systemProperty = (SystemProperty)ses.find(SystemProperty.SYSTEM_PROPERTY, id);
        return Optional.ofNullable(systemProperty);
    }


}

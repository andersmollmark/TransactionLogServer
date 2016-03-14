package com.delaval.usertransactionlogserver.persistence.entity;

import com.delaval.usertransactionlogserver.util.DateUtil;
import com.delaval.usertransactionlogserver.websocket.ClickLogContent;
import com.delaval.usertransactionlogserver.websocket.EventLogContent;
import com.delaval.usertransactionlogserver.websocket.WebSocketMessage;
import com.google.gson.Gson;
import simpleorm.dataset.SFieldString;
import simpleorm.dataset.SFieldTimestamp;
import simpleorm.dataset.SRecordMeta;
import simpleorm.sessionjdbc.SSessionJdbc;

import java.util.Date;

import static simpleorm.dataset.SFieldFlags.SDESCRIPTIVE;
import static simpleorm.dataset.SFieldFlags.SPRIMARY_KEY;

/**
 * Entity that mirrors LogContent-table
 */
public class EventLog extends AbstractEntity {

    public static final SRecordMeta EVENT_LOG = new SRecordMeta(EventLog.class, "EventLog");
    public static final SFieldString ID = new SFieldString(EVENT_LOG, "id", 100, SPRIMARY_KEY);
    public static final SFieldString NAME = new SFieldString(EVENT_LOG, "name", 40, SDESCRIPTIVE);
    public static final SFieldString CATEGORY = new SFieldString(EVENT_LOG, "category", 40, SDESCRIPTIVE);
    public static final SFieldString LABEL = new SFieldString(EVENT_LOG, "label", 512, SDESCRIPTIVE);
    public static final SFieldString TAB = new SFieldString(EVENT_LOG, "tab", 20, SDESCRIPTIVE);
    public static final SFieldString USER_TRANSACTION_KEY_ID = new SFieldString(EVENT_LOG, "userTransactionKeyId", 100, SDESCRIPTIVE);
    public static final SFieldTimestamp TIMESTAMP = new SFieldTimestamp(EVENT_LOG, "timestamp").overrideSqlDataType("TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");





    public String getId(){
        return getString(ID);
    }

    public String getUserTransactionKeyId(){
        return getString(USER_TRANSACTION_KEY_ID);
    }

    public String getTab(){
        return getString(TAB);
    }

    public String getName(){
        return getString(NAME);
    }

    public String getCategory(){
        return getString(CATEGORY);
    }

    public String getLabel(){
        return getString(LABEL);
    }

    public Date getTimestamp(){
        Date date = new Date();
        date.setTime(getTimestamp(TIMESTAMP).getTime());
        return date;
    }


    @Override
    public SRecordMeta<EventLog> getMeta() {
        return EVENT_LOG;
    }

    @Override
    public void createUserTransactionId(WebSocketMessage webSocketMessage) {
        setString(EventLog.USER_TRANSACTION_KEY_ID, getUserTransactionKeyId(webSocketMessage));
    }


}

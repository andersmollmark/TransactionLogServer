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

import static simpleorm.dataset.SFieldFlags.SDESCRIPTIVE;
import static simpleorm.dataset.SFieldFlags.SPRIMARY_KEY;

/**
 * Entity that mirrors ClickLog-table
 */
public class ClickLog extends AbstractEntity {

    public static final SRecordMeta CLICK_LOG = new SRecordMeta(ClickLog.class, "ClickLog");
    public static final SFieldString ID = new SFieldString(CLICK_LOG, "id", 100, SPRIMARY_KEY);
    public static final SFieldString INTERACTION_TYPE = new SFieldString(CLICK_LOG, "interactionType", 40, SDESCRIPTIVE);
    public static final SFieldString X = new SFieldString(CLICK_LOG, "x", 10, SDESCRIPTIVE);
    public static final SFieldString Y = new SFieldString(CLICK_LOG, "y", 10, SDESCRIPTIVE);
    public static final SFieldString CSS_CLASSNAME = new SFieldString(CLICK_LOG, "cssClassname", 100, SDESCRIPTIVE);
    public static final SFieldString ELEMENT_ID = new SFieldString(CLICK_LOG, "elementId", 50, SDESCRIPTIVE);
    public static final SFieldString USER_TRANSACTION_KEY_ID = new SFieldString(CLICK_LOG, "userTransactionKeyId", 100, SDESCRIPTIVE);
    public static final SFieldString TAB = new SFieldString(CLICK_LOG, "tab", 20, SDESCRIPTIVE);
    public static final SFieldTimestamp TIMESTAMP = new SFieldTimestamp(CLICK_LOG, "timestamp").overrideSqlDataType("TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");



    public String getId(){
        return getString(ID);
    }

    public String interactionType(){
        return getString(INTERACTION_TYPE);
    }

    public String getX(){
        return getString(X);
    }

    public String getY(){
        return getString(Y);
    }

    public String getCssClassName(){
        return getString(CSS_CLASSNAME);
    }

    public String getElementId(){
        return getString(ELEMENT_ID);
    }

    public String getUserTransactionKeyId(){
        return getString(USER_TRANSACTION_KEY_ID);
    }

    public String getTab(){
        return getString(TAB);
    }


    public Date getTimestamp(){
        Date date = new Date();
        date.setTime(getTimestamp(TIMESTAMP).getTime());
        return date;
    }


    @Override
    public SRecordMeta<ClickLog> getMeta() {
        return CLICK_LOG;
    }

    @Override
    public void createUserTransactionId(WebSocketMessage webSocketMessage) {
        setString(ClickLog.USER_TRANSACTION_KEY_ID, getUserTransactionKeyId(webSocketMessage));
    }


}

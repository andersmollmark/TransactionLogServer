package com.delaval.usertransactionlogserver.domain;

import com.delaval.usertransactionlogserver.persistence.entity.ClickLog;
import com.delaval.usertransactionlogserver.persistence.entity.EventLog;

import java.util.Date;

/**
 * Created by delaval on 12/9/2015.
 */
public class InternalEventLog {

    private String id;
    private String name;
    private String category;
    private String label;
    private String userTransactionKeyId;
    private Date timestamp;
    private String tab;

    public InternalEventLog(EventLog eventLog){
        id = eventLog.getId();
        name = eventLog.getName();
        category = eventLog.getCategory();
        label = eventLog.getLabel();
        tab = eventLog.getTab();
        userTransactionKeyId = eventLog.getUserTransactionKeyId();
        timestamp = eventLog.getTimestamp();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public String getUserTransactionKeyId() {
        return userTransactionKeyId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTab() {
        return tab;
    }
}

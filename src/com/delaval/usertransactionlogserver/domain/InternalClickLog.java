package com.delaval.usertransactionlogserver.domain;

import com.delaval.usertransactionlogserver.persistence.entity.ClickLog;

import java.util.Date;

/**
 * Created by delaval on 12/9/2015.
 */
public class InternalClickLog {


    private String id;
    private String x;
    private String y;
    private String cssClassName;
    private String elementId;
    private String userTransactionKeyId;
    private Date timestamp;
    private String tab;

    public InternalClickLog(ClickLog clickLog){
        id = clickLog.getId();
        x = clickLog.getX();
        y = clickLog.getY();
        cssClassName = clickLog.getCssClassName();
        elementId = clickLog.getElementId();
        tab = clickLog.getTab();
        userTransactionKeyId = clickLog.getUserTransactionKeyId();
        timestamp = clickLog.getTimestamp();
    }


    public String getId() {
        return id;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public String getElementId() {
        return elementId;
    }

    public String getUserTransactionKeyId() {
        return userTransactionKeyId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTab(){ return tab;}

}

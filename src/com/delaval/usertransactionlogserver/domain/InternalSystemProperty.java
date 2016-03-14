package com.delaval.usertransactionlogserver.domain;

import com.delaval.usertransactionlogserver.persistence.entity.AbstractEntity;
import com.delaval.usertransactionlogserver.persistence.entity.SystemProperty;
import com.delaval.usertransactionlogserver.websocket.WebSocketMessage;
import simpleorm.dataset.SFieldString;
import simpleorm.dataset.SFieldTimestamp;
import simpleorm.dataset.SRecordMeta;

import java.util.Date;

import static simpleorm.dataset.SFieldFlags.SDESCRIPTIVE;
import static simpleorm.dataset.SFieldFlags.SPRIMARY_KEY;

/**
 * Internal represantation of a SystemProperty-entity
 */
public class InternalSystemProperty {

    private String id;
    private String name;
    private String value;
    private Date timestamp;

    public InternalSystemProperty(){}

    public InternalSystemProperty(SystemProperty systemProperty){
        id = systemProperty.getId();
        name = systemProperty.getName();
        value = systemProperty.getValue();
        timestamp = systemProperty.getTimestamp();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public long getTimestampAsLong(){
        return timestamp != null ? timestamp.getTime() : 0L;
    }
}

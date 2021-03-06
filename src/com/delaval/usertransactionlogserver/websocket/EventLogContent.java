package com.delaval.usertransactionlogserver.websocket;

import com.delaval.usertransactionlogserver.util.DateUtil;

/**
 * Describes the content of the log from the webclient.
 */
public class EventLogContent implements JsonContent{

    private String eventName;
    private String eventCategory;
    private String eventLabel;
    private String timestamp;
    private String tab;

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTab() {
        return tab;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EventLogContent:")
                .append("eventName:").append(getEventName())
                .append("eventCategory:").append(getEventCategory())
                .append("eventLabel").append(getEventLabel())
                .append("activetab").append(getTab())
                .append(", time:").append(DateUtil.formatTimeStamp(Long.parseLong(getTimestamp())));
        return sb.toString();
    }


}

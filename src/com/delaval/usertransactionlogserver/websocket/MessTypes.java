package com.delaval.usertransactionlogserver.websocket;

/**
 * Different types of messages that can enter this application through a websocket.
 */
public enum MessTypes {

    IDLE_POLL("IdlePoll"),
    CLICK_LOG("clickLog"),
    SYSTEM_PROPERTY("systemProperty"),
    EVENT_LOG("eventLog");

    private String myValue;

    private MessTypes(String value){
        myValue = value;
    }

    public String getMyValue(){
        return myValue;
    }


    public boolean isSame(String value){
        return myValue.equals(value);
    }
}

package com.delaval.usertransactionlogserver.websocket;

import com.delaval.usertransactionlogserver.ServerProperties;
import com.delaval.usertransactionlogserver.jms.JmsResourceFactory;
import com.delaval.usertransactionlogserver.jms.producer.JmsMessageCreator;
import com.delaval.usertransactionlogserver.persistence.dao.OperationDAO;
import com.delaval.usertransactionlogserver.persistence.operation.CreateSystemPropertyOperation;
import com.delaval.usertransactionlogserver.persistence.operation.OperationFactory;
import com.delaval.usertransactionlogserver.persistence.operation.OperationParam;
import com.delaval.usertransactionlogserver.util.DateUtil;
import com.delaval.usertransactionlogserver.util.UtlsLogUtil;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The websocket that the w ebclient sends the message to.
 * If the message is a log that shall be saved, it post the message to the jms-queue.
 */
@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime = 10000)
public class UserTransactionLogWebSocket {

    private Session mySession;

    @OnWebSocketConnect
    public void onconnect(Session session) {
        mySession = session;

        UtlsLogUtil.info(this.getClass(), "CONNECTING session:" + getRemoteAddress());
        UtlsLogUtil.sessions.put(mySession, new Date());
        List<Session> sessionList = UtlsLogUtil.sessionsPerHost.get(getRemoteAddress());
        if(sessionList == null){
            sessionList = new ArrayList<>();
            UtlsLogUtil.sessionsPerHost.put(getRemoteAddress(), sessionList);
        }
        sessionList.add(mySession);
        checkSessionPerHost(sessionList);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        UtlsLogUtil.info(this.getClass(), "CLOSING websocket: " + getRemoteAddress() + ", status:" + statusCode + " reason:" + reason + " session.isOpen?" + mySession.isOpen());
        UtlsLogUtil.sessions.remove(mySession);
        List<Session> sessionList = UtlsLogUtil.sessionsPerHost.get(getRemoteAddress());
        sessionList.remove(mySession);
        checkSessionPerHost(sessionList);
        mySession = null;
    }

    private void checkSessionPerHost(List<Session> sessionList){
        UtlsLogUtil.debug(this.getClass(), "number of sessions in host:" + getRemoteAddress() + " is:" + sessionList.size() + " and they are created as follows:\n");
        sessionList.stream().forEach(i ->
          UtlsLogUtil.debug(this.getClass(), getRemoteAddress() + ", created:" + DateUtil.formatTimeStamp(UtlsLogUtil.sessions.get(i)) + " isOpen?" + i.isOpen()));
    }

    private String getRemoteAddress(){
     return mySession.getRemoteAddress() != null ? mySession.getRemoteAddress().getAddress().toString() : mySession.toString();
    }



    @OnWebSocketMessage
    public void handleMessage(Session session, String jsonMessage) {
        try {
            WebSocketMessage webSocketMessage = new Gson().fromJson(jsonMessage, WebSocketMessage.class);
            if (MessTypes.IDLE_POLL.isSame(webSocketMessage.getType())) {
//                if(getRemoteAddress().equals("/10.34.34.82")) {
//                    UtlsLogUtil.debug(this.getClass(), "Idle poll for:" + getRemoteAddress());
//                }
                session.getRemote().sendStringByFuture(jsonMessage);
                return;
            }
            UtlsLogUtil.debug(this.getClass(), "Incoming message:" + webSocketMessage.toString());
            // TODO authorization
             if (MessTypes.CLICK_LOG.isSame(webSocketMessage.getType())) {
                sendJmsTemplate(jsonMessage, ServerProperties.PropKey.JMS_QUEUE_DEST_CLICK, JmsResourceFactory.getClickLogInstance());
            } else if (MessTypes.EVENT_LOG.isSame(webSocketMessage.getType())) {
                sendJmsTemplate(jsonMessage, ServerProperties.PropKey.JMS_QUEUE_DEST_EVENT, JmsResourceFactory.getEventLogInstance());
            }else if (MessTypes.SYSTEM_PROPERTY.isSame(webSocketMessage.getType())) {
                OperationParam<CreateSystemPropertyOperation> createSystemPropertyParam = OperationFactory.getCreateSystemPropertyParam(webSocketMessage);
                OperationDAO.getInstance().executeOperation(createSystemPropertyParam);
            }else {
                UtlsLogUtil.info(this.getClass(), "Unknown message:" + webSocketMessage.getType());
            }

        } catch (Exception e) {
            UtlsLogUtil.info(this.getClass(), "Exception while handle websocketmessage:" + e.getMessage());
        }
    }

    private void sendJmsTemplate(String jsonMessage, ServerProperties.PropKey jmsDest, JmsResourceFactory jmsResourceFactory) {
        JmsMessageCreator messageCreator = new JmsMessageCreator(jsonMessage);
        JmsTemplate jmsTemplate = jmsResourceFactory.getJmsTemplate();
        String jmsDestination = getProp(jmsDest);
        jmsTemplate.send(jmsDestination, messageCreator);
    }

    private String getProp(ServerProperties.PropKey propKey) {
        return ServerProperties.getInstance().getProp(propKey);
    }
}

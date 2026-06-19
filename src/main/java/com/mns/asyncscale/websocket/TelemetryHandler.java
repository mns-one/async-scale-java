package com.mns.asyncscale.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class TelemetryHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = session.getUri().getQuery();

         if (clientId == null || clientId.isBlank()) {
            session.close(CloseStatus.BAD_DATA.withReason("Client ID missing"));
            return;
        }
        if(isUserConnected(clientId) || hasActiveConnection()){
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Wait for another simulation to end"));
            return;
        }
        sessions.put(clientId, session);
        log.info("User Connected! {}", clientId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = session.getUri().getQuery();

        if (clientId == null || clientId.isBlank()) {
            session.close(CloseStatus.BAD_DATA.withReason("Client ID missing"));
            return;
        }
        sessions.remove(clientId);
        log.info("User Disconnected! {}", clientId);
    }

    public void broadcast(String message) {

        for (Map.Entry<String, WebSocketSession> clientSession : sessions.entrySet()) {

            if (clientSession.getValue().isOpen()) {
                try {
                    clientSession.getValue().sendMessage(
                        new TextMessage(message)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isUserConnected(String clientID) {
        return sessions.containsKey(clientID);
    }

    public boolean hasActiveConnection() {
        return sessions.size() > 0;
    }

}


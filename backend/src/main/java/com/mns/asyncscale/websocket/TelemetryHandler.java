package com.mns.asyncscale.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.mns.asyncscale.dto.TelemetryDTO;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;


@Slf4j
@Component
public class TelemetryHandler extends TextWebSocketHandler {

    // class to maintain active socket connections and broadcast messages

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = session.getUri().getQuery();

         if (clientId == null || clientId.isBlank()) {
            session.close(CloseStatus.BAD_DATA.withReason("Client ID missing"));
            return;
        }
        if(isUserConnected(clientId)){
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

    public void broadcast(String clientId, TelemetryDTO message) {

        WebSocketSession clientSession = sessions.get(clientId);
        if(clientSession == null){
            return;
        }

        if (clientSession.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                clientSession.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
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


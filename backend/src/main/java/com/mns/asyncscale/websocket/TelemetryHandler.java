package com.mns.asyncscale.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final Object sessionLock = new Object();
    private final ExecutorService telemetryExecutor = Executors.newSingleThreadExecutor();
    private final int MAX_CONCURRENT_SOCKETS = 10;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = session.getUri().getQuery();

        if (clientId == null || clientId.isBlank()) {
            session.close(CloseStatus.BAD_DATA.withReason("Client ID missing"));
            return;
        }

        // atomic access to verify and put session in map
        synchronized(sessionLock) {
            if(isUserConnected(clientId)){
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Client already has a running session"));
                return;
            }
            if(sessions.size() >= MAX_CONCURRENT_SOCKETS) {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Server reached max clients, try again later"));
                return;
            }
            sessions.put(clientId, session);
        }

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

    // add scoket disconnection request to queue and return immediately
    public void disconnectClient(String clientId) {
        telemetryExecutor.execute(() -> closeClientSession(clientId));
    }

    private void closeClientSession(String clientId) {
        WebSocketSession clientSession = sessions.get(clientId);

        if (clientSession == null) {
            return;
        }

        try {
            clientSession.close();
        } catch (IOException e) {
            log.error("Failed to close websocket", e);
        } finally {
            sessions.remove(clientId);
        }
    }

    // add telemetry broadcast request to queue and return immediately
    public void broadcastAsync(String clientId, TelemetryDTO message) {
        telemetryExecutor.execute(() -> broadcast(clientId, message));
    }

    private void broadcast(String clientId, TelemetryDTO message) {

        WebSocketSession clientSession = sessions.get(clientId);
        if(clientSession == null){
            return;
        }

        if (clientSession.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                clientSession.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("Failed to send telemetry", e);
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


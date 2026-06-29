package com.mns.asyncscale.simulation;

import java.util.HashMap;

import org.springframework.stereotype.Component;


@Component
public class Manager {

    // using clientId as key for easy access to each clients simulation instance data
    private HashMap<String, ClientData> clients = new HashMap<>();
    private final int MAX_CONCURRENT_SIMULATIONS = 3;

    // check if simulation already running for client
    public boolean clientExists(String clientId) {
        return clients.containsKey(clientId);
    }

    public boolean serverAtMaxCapacity() {
        return clients.size() >= MAX_CONCURRENT_SIMULATIONS;
    }

    public void addClient(String clientId, ClientData data) {
        clients.put(clientId, data);
    }

    public void startClient(String clientId) {
        ClientData data = clients.get(clientId);
        data.getSeeder().start();
        data.getScaler().start();
    }

    // remove client entry after instance run is completed
    public void stopClient(String clientId) {
        clients.remove(clientId);
    }

    // update State flag to trigger client instance shutdown flow
    public void shutdownClientSim(String clientId) {
        if(clientExists(clientId)){
            ClientData data = clients.get(clientId);
            data.getState().triggerShutdown();
        }
    }

}

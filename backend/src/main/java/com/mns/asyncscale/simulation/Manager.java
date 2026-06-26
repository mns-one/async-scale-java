package com.mns.asyncscale.simulation;

import java.util.HashMap;

import org.springframework.stereotype.Component;


@Component
public class Manager {

    private HashMap<String, ClientData> clients = new HashMap<>();

    // insert
    public void addClient(String clientId, ClientData data) {
        clients.put(clientId, data);
    }

    // check
    public boolean clientExists(String clientId) {
        return clients.containsKey(clientId);
    }

    // delete
    public void stopClient(String clientId) {
        clients.remove(clientId);
    }

    public void startClient(String clientId) {
        ClientData data = clients.get(clientId);
        data.getSeeder().start();
        data.getScaler().start();
    }

    public void createClient(String clientId) {

    }

}

package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;


@Component
public class StopSim {
    
    private final Manager manager;

    public StopSim(Manager manager) {
        this.manager = manager;
    }

    public void shutdown(String clientId) {
        manager.shutdownClientSim(clientId);
    }

}

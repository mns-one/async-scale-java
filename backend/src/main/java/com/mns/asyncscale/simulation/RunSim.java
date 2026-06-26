package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.exception.CustomException;
import com.mns.asyncscale.websocket.TelemetryHandler;

@Component
public class RunSim {

    private final TelemetryHandler telemetryHandler;
    private final Manager manager;

    RunSim(TelemetryHandler telemetryHandler, Manager manager) {
        this.telemetryHandler = telemetryHandler;
        this.manager = manager;
    }

    public void start(SimRequestDTO payload) { 

        if(manager.clientExists(payload.getClientId())){
            throw new CustomException("Simulation already running for clientId: " + payload.getClientId());
        }

        System.out.println("Initializing Sim for clientId: " + payload.getClientId());
        
        // create sim state obj and pass to seeder and scaler
        State state = new State(
            payload.getClientId(),
            payload.getPacketSize(),
            payload.getSeedInterval(),
            payload.getTotalPackets(),
            payload.getProcessTarget()
        );

        Seeder seeder = new Seeder(state);
        Thread seederThread = new Thread(seeder);

        Scaler scaler = new Scaler(state, manager, telemetryHandler);
        Thread scalerThread = new Thread(scaler);

        ClientData clientData = new ClientData(state, seederThread, scalerThread);

        manager.addClient(payload.getClientId(), clientData);
        manager.startClient(payload.getClientId());

        System.out.println("Initializing Complete!");

    }

}


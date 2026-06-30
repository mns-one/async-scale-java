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

        // return early if one instance is already running for client
        if(manager.clientExists(payload.getClientId())){
            System.out.println("Instance already running for ClientId - " + payload.getClientId());
            return;
        }

        System.out.println("Initializing Sim for clientId: " + payload.getClientId());
        
        // create simulation State obj and Seeder, Scaler threads and store them in ClientData
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

        // add clientData to Manager and trigger the simulation instance
        manager.addClient(payload.getClientId(), clientData);
        manager.startClient(payload.getClientId());

        System.out.println("Initializing Complete!");

    }

    public boolean isServerFull() {
        return manager.serverAtMaxCapacity();
    }

}


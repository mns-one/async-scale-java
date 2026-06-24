package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.websocket.TelemetryHandler;

@Component
public class RunSim {

    private final TelemetryHandler telemetry;
    private State state;
    private SeedDb seedDb;
    private Scaler scaler;

    RunSim(TelemetryHandler telemetry, SeedDb seedDb, State state, Scaler scaler) {
        this.telemetry = telemetry;
        this.seedDb = seedDb;
        this.state = state;
        this.scaler = scaler;
    }

    public void start(SimRequestDTO payload) {
        telemetry.broadcast("Starting Sim...");
        System.out.println("Initializing Sim...");
        
        state.setState(payload.getPacketSize(), payload.getSeedInterval(), payload.getTotalPackets(), payload.getProcessTarget());

        Thread seederThread = new Thread(seedDb);
        seederThread.start();

        Thread scalerThread = new Thread(scaler);
        scalerThread.start();

        System.out.println("Initializing Complete!");

        // start scaler.java

    }

}

 
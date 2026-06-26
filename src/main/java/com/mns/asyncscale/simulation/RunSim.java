package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.websocket.TelemetryHandler;

@Component
public class RunSim {

    private final TelemetryHandler telemetry;

    RunSim(TelemetryHandler telemetry) {
        this.telemetry = telemetry;
    }

    public void start(SimRequestDTO payload) {
        telemetry.broadcast("Starting Sim...");
        System.out.println("Initializing Sim...");
        
        // create sim state obj and pass to seeder and scaler
        State state = new State(
            payload.getPacketSize(),
            payload.getSeedInterval(),
            payload.getTotalPackets(),
            payload.getProcessTarget()
        );

        Seeder seeder = new Seeder(state);
        Thread seederThread = new Thread(seeder);
        seederThread.start();

        Scaler scaler = new Scaler(state);
        Thread scalerThread = new Thread(scaler);
        scalerThread.start();

        System.out.println("Initializing Complete!");

    }

}


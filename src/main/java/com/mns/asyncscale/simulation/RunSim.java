package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

import com.mns.asyncscale.websocket.TelemetryHandler;

@Component
public class RunSim {

    private final TelemetryHandler telemetry;

    RunSim(TelemetryHandler telemetry) {
        this.telemetry = telemetry;
    }

    public void start() {
        telemetry.broadcast("Started Sim...");
    }
    
}

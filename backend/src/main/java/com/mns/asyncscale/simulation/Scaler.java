package com.mns.asyncscale.simulation;

import com.mns.asyncscale.websocket.TelemetryHandler;
import com.mns.asyncscale.dto.TelemetryDTO;

public class Scaler implements Runnable {

    private State state;
    private final Manager manager;
    private final TelemetryHandler telemetryHandler;

    public Scaler(State state, Manager manager, TelemetryHandler telemetryHandler) {
        this.state = state;
        this.manager = manager;
        this.telemetryHandler = telemetryHandler;
    }

    public void run() {
        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {

        System.out.println("Scaler running...");
        int packetCount = 0;

        while(state.jobsLeft()){
            State.StateSnapshot snapshot = state.getSnapshot();
            if(snapshot.shutdown()) {
                stopWorkers();
                break;
            }
            // System.out.println("Available_Jobs = " + snapshot.availableJobs() + " || Running_Workers = " + snapshot.activeWorkers());
            
            int totalJobs = snapshot.availableJobs() + snapshot.inProcessJobs();
            
            sendTelemetry(snapshot, totalJobs, packetCount);

            int desired = 0;

            if (totalJobs > 0) {
                desired = (int) Math.ceil((snapshot.processTarget() / 100.0) * totalJobs);
                desired = Math.max(1, desired);
            }

            int diff = desired - snapshot.activeWorkers();

            if(diff > 0) {
                for(int i=0; i<diff; i++){
                    state.addActiveWorkers(1);
                    Worker worker = new Worker(state);
                    Thread workerThread = new Thread(worker);
                    workerThread.start();
                }
            }
            else if(diff < 0) {
                int tokens = Math.abs(diff);
                state.addStopTokens(tokens);
            }

            Thread.sleep(500);
            packetCount++;

        }

        State.StateSnapshot snapshot = state.getSnapshot();
        int totalJobs = snapshot.availableJobs() + snapshot.inProcessJobs();     
        sendTelemetry(snapshot, totalJobs, packetCount);

        manager.stopClient(state.getClientId());

        System.out.println("Scaler stopped!");

    }

    public void stopWorkers() throws InterruptedException {

        while(true){
            State.StateSnapshot snapshot = state.getSnapshot();
            if(snapshot.activeWorkers() == 0) break;
            state.addStopTokens(snapshot.activeWorkers());
            Thread.sleep(1000);
        }

    }

    public void sendTelemetry(State.StateSnapshot snapshot, int totalJobs, int packetCount) {

        TelemetryDTO telemetryDTO = new TelemetryDTO(packetCount, System.currentTimeMillis(),
                    totalJobs, snapshot.activeWorkers(), snapshot.completedJobs(), snapshot.newJobs());

        telemetryHandler.broadcast(snapshot.clientId(), telemetryDTO);

    }
    
}

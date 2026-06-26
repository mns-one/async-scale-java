package com.mns.asyncscale.simulation;

import com.mns.asyncscale.websocket.TelemetryHandler;
import com.mns.asyncscale.websocket.TelemetryRecord;

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

        while(state.jobInflow()){

            State.StateSnapshot snapshot = state.getSnapshot();
            System.out.println("Available_Jobs = " + snapshot.availableJobs() + " || Running_Workers = " + snapshot.activeWorkers());
            
            int totalJobs = snapshot.availableJobs() + snapshot.inProcessJobs();
            
            TelemetryRecord telemetryRecord = new TelemetryRecord(totalJobs, snapshot.activeWorkers(), snapshot.completedJobs());
            telemetryHandler.broadcast(snapshot.clientId(), telemetryRecord);

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

        }

        manager.stopClient(state.getClientId());

        System.out.println("Scaler stopped!");

    }
    
}

package com.mns.asyncscale.simulation;

import com.mns.asyncscale.websocket.TelemetryHandler;
import com.mns.asyncscale.dto.TelemetryDTO;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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
        int packetCount = 0; // sequence counter for sending telemetry packets

        while(state.jobsLeft()){

            State.StateSnapshot snapshot = state.getSnapshot();

            // check for shutdown flag before each iteration
            if(snapshot.shutdown()) {
                stopWorkers();
                break;
            }
            // System.out.println("Available_Jobs = " + snapshot.availableJobs() + " || Running_Workers = " + snapshot.activeWorkers());
            
            // broadcast current state of simulation
            int totalJobs = snapshot.availableJobs() + snapshot.inProcessJobs();
            sendTelemetry(snapshot, totalJobs, packetCount);

            // calcuate number of workers needed to maintain defined target
            int desired = 0;

            if (totalJobs > 0) {
                desired = (int) Math.ceil((snapshot.processTarget() / 100.0) * totalJobs);
                desired = Math.max(1, desired);
            }

            int diff = desired - snapshot.activeWorkers();

            // create more workers if needed
            if(diff > 0) {
                for(int i=0; i<diff; i++){
                    Worker worker = new Worker(state);
                    try{
                        Thread workerThread = new Thread(worker);
                        workerThread.start();
                        state.addActiveWorkers(1);
                    }
                    catch (OutOfMemoryError e) {
                        log.error("Server out of Memory, exiting worker thread creation loop!", e);
                        break;
                    }
                    catch(Exception e){
                        log.error("Error creating new worker thread", e);
                    }
                }
            }
            // else raise flag for worker threads to exit
            else if(diff < 0) {
                int tokens = Math.abs(diff);
                state.addStopTokens(tokens);
            }

            Thread.sleep(500);
            packetCount++;

        }

        // broadcast final state after loop ends
        State.StateSnapshot snapshot = state.getSnapshot();
        int totalJobs = snapshot.availableJobs() + snapshot.inProcessJobs();     
        sendTelemetry(snapshot, totalJobs, packetCount);

        // remove client entry from Manager
        manager.stopClient(state.getClientId());
        telemetryHandler.disconnectClient(snapshot.clientId());

        System.out.println("Scaler stopped!");

    }

    // let currently active workers finish first after shutodwn trigger mid execution
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

        telemetryHandler.broadcastAsync(snapshot.clientId(), telemetryDTO);

    }
    
}

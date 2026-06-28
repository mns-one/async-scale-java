package com.mns.asyncscale.simulation;


public class Seeder implements Runnable {

    private State state;

    public Seeder(State state) {
        this.state = state;
    }

    public void run() {
        try {
            state.seederRunning();
            start();
            state.seederStopped();
        } catch (InterruptedException e) {
            state.seederStopped();
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {

        // for loop that adds packetSize to available_jobs after every seedInterval
        State.StateSnapshot snapshot = state.getSnapshot();

        int totalPackets = snapshot.totalPackets();
        int packetSize = snapshot.packetSize();
        int seedInterval = snapshot.seedInterval();

        for(int i=0; i<totalPackets; i++) {

            // check for shutdown flag before each iteration
            State.StateSnapshot snapshot_check_shutdown = state.getSnapshot();
            if(snapshot_check_shutdown.shutdown()) break;

            // add new jobs to state and sleep
            int newJobs = 1 + (int) (Math.random() * packetSize);
            state.addAvailableJobs(newJobs);
            System.out.println("Jobs added -> " + newJobs);
            state.setNewJobs(newJobs);

            Thread.sleep(1 + (long) (Math.random() * seedInterval));

        }

        System.out.println("Seeder stopped!");

    }
    
}



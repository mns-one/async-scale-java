package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

@Component
public class SeedDb implements Runnable {

    private State state;

    public SeedDb(State state) {
        this.state = state;
    }

    public void run() {
        final int packetSize = state.getPacketSize();
        final int seedInterval = state.getSeedInterval();
        final int totalPackets = state.getTotalPackets();
        try {
            seed_data(packetSize, seedInterval, totalPackets);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void seed_data(int packetSize, int seedInterval, int totalPackets) throws InterruptedException {

        // for loop that adds packetSize to available_jobs after every time_interval
        state.setSeederActive(true);

        for(int i=0; i<totalPackets; i++) {
            state.addAvailableJobs(packetSize);
            System.out.println(state.getAvailableJobs());
            Thread.sleep(seedInterval);
        }

        state.setSeederActive(false);
        System.out.println("Seeder stopped!");

    }
    
}



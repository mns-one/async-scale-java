package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class State {

    private int packetSize;
    private int seedInterval;
    private int totalPackets;

    private int availableJobs;
    private int inProcessJobs;
    private int completedJobs;

    private int processTarget;

    private boolean seederActive;

    public void setState(int packetSize, int seedInterval, int totalPackets, int processTarget) {
        this.packetSize = packetSize;
        this.seedInterval = seedInterval;
        this.totalPackets = totalPackets;
        this.processTarget = processTarget;
        this.availableJobs = 0;
        this.inProcessJobs = 0;
        this.completedJobs = 0;
    }

    public synchronized void setAvailableJobs(int newJobs) {
        this.availableJobs = newJobs;
    }

    public synchronized int getAvailableJobs() {
        return this.availableJobs;
    }

    public synchronized void addAvailableJobs(int count) {
        this.availableJobs += count;
    }

    public synchronized void removeAvailableJobs(int count) {
        this.availableJobs -= count;
    }
    

}

// make snapshot method
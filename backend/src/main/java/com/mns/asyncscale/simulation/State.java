package com.mns.asyncscale.simulation;


public class State {

    private String clientId;           // unique identifier for each connected client

    private int packetSize;            // max number of jobs added by one Seeder loop
    private int seedInterval;          // max Seeder thread sleep time between every loop
    private int totalPackets;          // number of times Seeder loop will execute to add jobs

    private int newJobs;               // number of jobs added by Seeder most recently
    private int availableJobs;         // jobs queued up for processing
    private int inProcessJobs;         // jobs claimed by workers and currently under process
    private int completedJobs;         // finished jobs

    private int processTarget;         // target % reference for Scaler to scale workers

    private int activeWorkers;         // counter for currently active worker threads
    private int stopToken;             // flag for requesting a worker thread to exit

    private boolean seederStatus;      // flag to check for job inflow
    private boolean shutdown;          // flag to trigger graceful shutdown of simulation instance

    
    public State(String clientId, int packetSize, int seedInterval, int totalPackets, int processTarget) {
        this.clientId = clientId;
        this.packetSize = packetSize;
        this.seedInterval = seedInterval;
        this.totalPackets = totalPackets;
        this.processTarget = processTarget;
        this.newJobs = 0;
        this.availableJobs = 0;
        this.inProcessJobs = 0;
        this.completedJobs = 0;
        this.activeWorkers = 0;
        this.stopToken = 0;
        this.seederStatus = false;
        this.shutdown = false;
    }

    public void triggerShutdown() {
        this.shutdown = true;
    }

    public String getClientId() {
        return this.clientId;
    }

    // job inflow methods
    public synchronized void seederRunning() {
        this.seederStatus = true;
    }

    public synchronized void seederStopped() {
        this.seederStatus = false;
    }

    public synchronized boolean jobsLeft() {
        return this.seederStatus || this.availableJobs > 0 || this.activeWorkers > 0;
    }

    public synchronized void setNewJobs(int count) {
        this.newJobs = count;
    }

    // job count methods
    public synchronized void addAvailableJobs(int count) {
        this.availableJobs += count;
    }

    public synchronized void removeAvailableJobs(int count) {
        this.availableJobs -= count;
    }


    // worker methods
    public synchronized void addStopTokens(int count) {
        this.stopToken += count;
    }

    public synchronized boolean shouldStop() {
        if(this.stopToken > 0) {
            this.stopToken--;
            this.activeWorkers--;
            return true;
        }
        else{
            return false;
        }
    }

    public synchronized boolean claimJob() {
        if(this.availableJobs > 0){
            this.availableJobs--;
            this.inProcessJobs++;
            return true;
        }
        else{
            return false;
        }
    }

    public synchronized void finishJob() {
        this.inProcessJobs--;
        this.completedJobs++;
    }

    public synchronized void addActiveWorkers(int count) {
        this.activeWorkers += count;
    }

    
    // snapshot
    public synchronized StateSnapshot getSnapshot() {
        return new StateSnapshot(clientId, packetSize, seedInterval, totalPackets,
            newJobs, availableJobs, inProcessJobs, completedJobs, processTarget, seederStatus, activeWorkers, stopToken, shutdown);
    }

    public record StateSnapshot(String clientId, int packetSize, int seedInterval, int totalPackets,
                                int newJobs, int availableJobs, int inProcessJobs, int completedJobs,
                                int processTarget, boolean seederStatus, int activeWorkers, int stopToken, boolean shutdown){}
    

}

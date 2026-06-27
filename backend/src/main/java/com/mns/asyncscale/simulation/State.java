package com.mns.asyncscale.simulation;


public class State {

    private String clientId;
    private int packetSize;
    private int seedInterval;
    private int totalPackets;

    private int newJobs;
    private int availableJobs;
    private int inProcessJobs;
    private int completedJobs;

    private int processTarget;

    private int activeWorkers;
    private int stopToken;

    private boolean seederStatus;

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

    public synchronized boolean jobInflow() {
        return this.seederStatus || this.availableJobs > 0;
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
            newJobs, availableJobs, inProcessJobs, completedJobs, processTarget, seederStatus, activeWorkers, stopToken);
    }

    public record StateSnapshot(String clientId, int packetSize, int seedInterval, int totalPackets,
                                int newJobs, int availableJobs, int inProcessJobs, int completedJobs,
                                int processTarget, boolean seederStatus, int activeWorkers, int stopToken){}
    

}

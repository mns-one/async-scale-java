package com.mns.asyncscale.websocket;
import lombok.Data;

@Data
public class TelemetryRecord {

    int totalJobs;
    int activeWorkers;
    int completedJobs;
    
    public TelemetryRecord(int totalJobs, int activeWorkers, int completedJobs) {
        this.totalJobs = totalJobs;
        this.activeWorkers = activeWorkers;
        this.completedJobs = completedJobs;
    }

    
}

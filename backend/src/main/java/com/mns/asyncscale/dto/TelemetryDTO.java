package com.mns.asyncscale.dto;
import lombok.Data;

@Data
public class TelemetryDTO {

    private Integer seq;
    private Long ts;
    private Integer totalJobs;
    private Integer activeWorkers;
    private Integer completedJobs;
    private Integer newJobs;

    public TelemetryDTO(int seq, long ts, int totalJobs, int activeWorkers, int completedJobs, int newJobs) {
        this.seq = seq;
        this.ts = ts;
        this.totalJobs = totalJobs;
        this.activeWorkers = activeWorkers;
        this.completedJobs = completedJobs;
        this.newJobs = newJobs;
    }

    
}

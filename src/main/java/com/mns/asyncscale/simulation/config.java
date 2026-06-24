package com.mns.asyncscale.simulation;

import lombok.Data;

@Data
public class config {

    private int packet_size;
    private int target;
    private int time_interval;
    private int available_jobs;
    private int jobs_in_process;
    private int completed_jobs;
    private int workers;
    private int stop_token;

}



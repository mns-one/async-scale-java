package com.mns.asyncscale.simulation;

import org.springframework.stereotype.Component;

@Component
public class Scaler implements Runnable {

    private State state;

    public Scaler(State state) {
        this.state = state;
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

        while(state.getAvailableJobs() != 0 || state.isSeederActive() != false){

            int availableJobs = state.getAvailableJobs();
            int completedJobs = state.getCompletedJobs();


            

            Thread.sleep(500);

        }

        System.out.println("Scaler stopped!");

    }
    
}

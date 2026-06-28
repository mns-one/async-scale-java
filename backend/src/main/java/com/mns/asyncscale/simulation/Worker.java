package com.mns.asyncscale.simulation;


public class Worker implements Runnable {

    private State state;

    public Worker(State state) {
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

        // check for exit flag before claiming new jobs
        while(!state.shouldStop()) {

            if(state.claimJob()){
                // simulate IO delay for processing after claiming a job
                Thread.sleep(1 + (long) (Math.random() * 5000));
                state.finishJob();
            }
            else{
                Thread.sleep(1000);
            }

        }
        
    }
    
}

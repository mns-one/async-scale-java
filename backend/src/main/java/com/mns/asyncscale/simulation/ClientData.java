package com.mns.asyncscale.simulation;

import lombok.Data;

@Data
public class ClientData {

    private State state;
    private Thread seeder;
    private Thread scaler;

    public ClientData(State state, Thread seeder, Thread scaler) {
        this.state = state;
        this.seeder = seeder;
        this.scaler = scaler;
    }
    
}

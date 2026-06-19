package com.mns.asyncscale.service;

import org.springframework.stereotype.Service;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.dto.SimResponseDTO;
import com.mns.asyncscale.simulation.RunSim;

@Service
public class SimService {

    private final RunSim runSim;

    SimService(RunSim runSim) {
        this.runSim = runSim;
    }

    public SimResponseDTO runSimService(SimRequestDTO payload) {

        SimResponseDTO res = new SimResponseDTO("Started!");
        runSim.start();
        return res;

    }

    
}

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

        runSim.start(payload);

        SimResponseDTO res = new SimResponseDTO("Initialized Simulation");
        return res;

    }

}

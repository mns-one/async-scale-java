package com.mns.asyncscale.service;

import org.springframework.stereotype.Service;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.dto.SimStopRequestDTO;
import com.mns.asyncscale.dto.SimResponseDTO;
import com.mns.asyncscale.simulation.RunSim;
import com.mns.asyncscale.simulation.StopSim;

@Service
public class SimService {

    private final RunSim runSim;
    private final StopSim stopSim;

    SimService(RunSim runSim, StopSim stopSim) {
        this.runSim = runSim;
        this.stopSim = stopSim;
    }

    public SimResponseDTO runSimService(SimRequestDTO payload) {

        runSim.start(payload);

        SimResponseDTO res = new SimResponseDTO("Initialized Simulation");
        return res;

    }

    public void stopSimService(SimStopRequestDTO payload) {

        stopSim.shutdown(payload.getClientId());

    }

}

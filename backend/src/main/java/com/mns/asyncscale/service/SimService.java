package com.mns.asyncscale.service;

import org.springframework.stereotype.Service;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.dto.SimStopRequestDTO;
import com.mns.asyncscale.exception.CustomException;
import com.mns.asyncscale.exception.RateLimitExceededException;
import com.mns.asyncscale.rateLimiter.RateLimiterService;
import com.mns.asyncscale.dto.SimResponseDTO;
import com.mns.asyncscale.simulation.RunSim;
import com.mns.asyncscale.simulation.StopSim;

@Service
public class SimService {

    private final RunSim runSim;
    private final StopSim stopSim;
    private final RateLimiterService rateLimiterService;

    SimService(RunSim runSim, StopSim stopSim, RateLimiterService rateLimiterService) {
        this.runSim = runSim;
        this.stopSim = stopSim;
        this.rateLimiterService = rateLimiterService;
    }

    public SimResponseDTO runSimService(SimRequestDTO payload) {

        if(runSim.isServerFull()) {
            throw new CustomException("Server running at max capacity, try again later");
        }

        if(!rateLimiterService.allowed(payload.getClientId())) {
            throw new RateLimitExceededException("Usage limit reached, Try again later");
        }

        runSim.start(payload);

        SimResponseDTO res = new SimResponseDTO("Initialized Simulation");
        return res;

    }

    public void stopSimService(SimStopRequestDTO payload) {

        stopSim.shutdown(payload.getClientId());

    }

}

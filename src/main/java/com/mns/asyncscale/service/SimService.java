package com.mns.asyncscale.service;

import org.springframework.stereotype.Service;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.dto.SimResponseDTO;

@Service
public class SimService {

    public SimResponseDTO runSimService(SimRequestDTO payload) {

        SimResponseDTO res = new SimResponseDTO("Started!");
        return res;
    }

    
}

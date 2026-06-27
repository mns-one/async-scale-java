package com.mns.asyncscale.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.mns.asyncscale.dto.SimRequestDTO;
import com.mns.asyncscale.dto.SimResponseDTO;
import com.mns.asyncscale.service.SimService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class SimController {

    private final SimService simService;

    SimController (SimService simService) {
        this.simService = simService;
    }

    @PostMapping("/run")
    public SimResponseDTO runSim(@Valid @RequestBody SimRequestDTO req) {
        
        SimResponseDTO res = simService.runSimService(req);
        
        return res;
    }
    
}

package com.mns.asyncscale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimRequestDTO {

    @NotNull
    private String clientId;

    @NotNull
    @Min(50)
    @Max(1000)
    private Integer packetSize;

    @NotNull
    @Min(2000)
    @Max(20000)
    private Integer seedInterval;

    @NotNull
    @Min(2)
    @Max(10)
    private Integer totalPackets;

    @NotNull
    @Min(10)
    @Max(80)
    private Integer processTarget;
    
}


package com.mns.asyncscale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimRequestDTO {

    @NotNull
    private String clientId;

    @NotNull
    @Min(1)
    private Integer packetSize;

    @NotNull
    @Min(1)
    private Integer seedInterval;

    @NotNull
    @Min(1)
    private Integer totalPackets;

    @NotNull
    @Min(1)
    private Integer processTarget;
    
}


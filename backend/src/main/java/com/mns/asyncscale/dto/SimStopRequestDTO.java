package com.mns.asyncscale.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimStopRequestDTO {
    @NotNull
    private String clientId;
}

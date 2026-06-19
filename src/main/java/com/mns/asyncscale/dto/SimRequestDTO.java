package com.mns.asyncscale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimRequestDTO {

    @NotNull
    @Min(1)
    private Integer size;

    @NotNull
    @Min(1)
    private Integer interval;

    @NotNull
    @Min(1)
    private Integer target;
    
}

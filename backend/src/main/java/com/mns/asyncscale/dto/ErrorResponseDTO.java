package com.mns.asyncscale.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponseDTO {

    private final LocalDateTime timestamp;
    private final String error;
    private final String message;
    private final Map<String, String> fieldErrors;
    
}

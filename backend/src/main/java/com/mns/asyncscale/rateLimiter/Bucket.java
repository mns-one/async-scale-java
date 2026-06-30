package com.mns.asyncscale.rateLimiter;

import lombok.Data;

@Data
public class Bucket {

    private int usage;
    private long windowStartTimeMillis;

    public Bucket(int usage, long windowStartTimeMillis) {
        this.usage = usage;
        this.windowStartTimeMillis = windowStartTimeMillis;
    }
    
}

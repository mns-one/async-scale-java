package com.mns.asyncscale.rateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class RateLimiterService {

    private ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    private final long MAX_WINDOW_TIME_MILLIS;
    private final int MAX_USAGE;

    private long lastCleanup = 0;
    private final long CLEANUP_INTERVAL_MILLIS;;

    public RateLimiterService(@Value("${rate.limit.window.size.millis}") long maxWindowTimeMillis,
                            @Value("${rate.limit.max.usage}") int maxUsage,
                            @Value("${rate.limit.cleanup.interval.millis}") long cleanupIntervalMillis)
    {
        MAX_WINDOW_TIME_MILLIS = maxWindowTimeMillis;
        MAX_USAGE = maxUsage;
        CLEANUP_INTERVAL_MILLIS = cleanupIntervalMillis;
    }

    public boolean allowed(String clientId) {

        long currTime = System.currentTimeMillis();

        // clenup stale entries 
        if (currTime - lastCleanup >= CLEANUP_INTERVAL_MILLIS) {
            cleanupBuckets(currTime);
            lastCleanup = currTime;
        }

        Bucket updatedBucket = buckets.compute(clientId, (key, bucket) -> {
            // create bucket for new client
            if (bucket == null) {
                return new Bucket(1, currTime);
            }

            long windowSize = currTime - bucket.getWindowStartTimeMillis();

            // increment usage if within window time limit
            if (windowSize <= MAX_WINDOW_TIME_MILLIS) {
                if (bucket.getUsage() <= MAX_USAGE) {
                    bucket.setUsage(bucket.getUsage() + 1);
                }
                return bucket;
            }

            // if previous window expired, reset the bucket
            bucket.setUsage(1);
            bucket.setWindowStartTimeMillis(currTime);
            return bucket;
        });

        return updatedBucket.getUsage() <= MAX_USAGE;

    }

    private void cleanupBuckets(long currTime) {
        
        for(Map.Entry<String, Bucket> entry : buckets.entrySet()) {

            long diff = currTime - entry.getValue().getWindowStartTimeMillis();
            if(diff > MAX_WINDOW_TIME_MILLIS) {
                buckets.remove(entry.getKey());
            }

        }

    }
    
}

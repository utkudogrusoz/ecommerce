package com.utkudogrusoz.ecommerce.Core.Service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, Instant> requestTimes = new ConcurrentHashMap<>();
    private final int REQUEST_LIMIT = 1; // Her 10 saniyede 1 istek
    private final int TIME_WINDOW = 10; // Saniye cinsinden

    public boolean isAllowed(String ipAddress) {
        Instant now = Instant.now();
        if (requestTimes.containsKey(ipAddress)) {
            Instant lastRequestTime = requestTimes.get(ipAddress);
            if (now.isBefore(lastRequestTime.plusSeconds(TIME_WINDOW))) {
                return false;
            }
        }
        requestTimes.put(ipAddress, now);
        return true;
    }
}

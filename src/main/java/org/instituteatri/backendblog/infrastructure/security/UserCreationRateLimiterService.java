package org.instituteatri.backendblog.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UserCreationRateLimiterService {

    private final Map<String, AtomicInteger> userCreationAttempts = new ConcurrentHashMap<>();

    public boolean allowUserCreation(String ipAddress) {
        userCreationAttempts.putIfAbsent(ipAddress, new AtomicInteger(0));
        AtomicInteger attempts = userCreationAttempts.get(ipAddress);

        int maxAttempts = 3;
        return attempts.incrementAndGet() <= maxAttempts;
    }
}

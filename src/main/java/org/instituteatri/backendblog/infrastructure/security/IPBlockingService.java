package org.instituteatri.backendblog.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.BlockedIP;
import org.instituteatri.backendblog.repository.BlockedIPRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IPBlockingService {

    private final BlockedIPRepository blockedIPRepository;
    private final IPResolverService ipResolverService;


    public void registerFailedAttempt(String ipAddress, String userAgent) {
        BlockedIP blockedIP = blockedIPRepository.findByIpAddress(ipAddress);
        if (blockedIP == null) {
            blockedIP = new BlockedIP();
            blockedIP.setIpAddress(ipAddress);
        }
        blockedIP.setFailedAttempts(blockedIP.getFailedAttempts() + 1);
        blockedIP.setUserAgent(userAgent);
        blockedIPRepository.save(blockedIP);
        log.warn("[BLOCKED_IP] Failed attempt from IP address: {} and User-Agent: {} - Attempt count: {}", ipAddress, userAgent, blockedIP.getFailedAttempts());

        if (blockedIP.getFailedAttempts() >= 3) {
            log.warn("[BLOCKED_IP] IP address blocked due to multiple failed attempts: {}", ipAddress);
        }
    }
    public boolean isBlocked(String ipAddress) {
        BlockedIP blockedIP = blockedIPRepository.findByIpAddress(ipAddress);
        return blockedIP != null && blockedIP.getFailedAttempts() >= 3;
    }

    public String getRealClientIP() {
        return ipResolverService.getRealClientIP();
    }
}

package org.instituteatri.backendblog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class BlockedIP implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String ipAddress;
    private int failedAttempts;
    private String userAgent;
    private Instant lastFailedAttemptTimestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockedIP blockedIP = (BlockedIP) o;
        return failedAttempts == blockedIP.failedAttempts &&
                Objects.equals(id, blockedIP.id) &&
                Objects.equals(ipAddress, blockedIP.ipAddress) &&
                Objects.equals(userAgent, blockedIP.userAgent) &&
                Objects.equals(lastFailedAttemptTimestamp, blockedIP.lastFailedAttemptTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ipAddress, failedAttempts, userAgent, lastFailedAttemptTimestamp);
    }

    @Override
    public String toString() {
        return String.format("BlockedIP(id=%s, ipAddress=%s, failedAttempts=%d, userAgent=%s, lastFailedAttemptTimestamp=%s)",
                id, ipAddress, failedAttempts, userAgent, lastFailedAttemptTimestamp);
    }
}

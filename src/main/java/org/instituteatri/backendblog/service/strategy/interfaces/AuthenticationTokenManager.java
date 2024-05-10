package org.instituteatri.backendblog.service.strategy.interfaces;


import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;

public interface AuthenticationTokenManager {
    TokenResponseDTO generateTokenResponse(User user);
    void revokeAllUserTokens(User user);
}

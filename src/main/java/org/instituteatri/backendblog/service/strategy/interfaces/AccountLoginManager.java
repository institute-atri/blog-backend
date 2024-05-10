package org.instituteatri.backendblog.service.strategy.interfaces;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.LoginRequestDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

public interface AccountLoginManager {

    Authentication authenticateUser(LoginRequestDTO authDto, AuthenticationManager authManager);

    void handleSuccessfulLogin(User user);

    ResponseEntity<TokenResponseDTO> handleBadCredentials(String email);
}

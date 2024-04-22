package org.instituteatri.backendblog.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomLogoutHandler is responsible for handling the logout process.
 * It recovers the token from the request, validates it, and then invalidates it.
 * If the token is invalid, an unauthorized response is sent.
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final SecurityFilter securityFilter;
    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = securityFilter.recoverToken(request);

        if (token!= null) {
            try {
                tokenService.isValidateToken(token);
                tokenService.invalidateToken(token);
            } catch (TokenInvalidException e) {
                try {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(e.getMessage());
                } catch (IOException ioException) {
                    throw new TokenGenerationException("Error while writing response", ioException);
                }
            }
        }
    }
}

package org.instituteatri.backendblog.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final SecurityFilter securityFilter;
    private final TokenRepository tokenRepository;
    private static final String TOKEN_EXPIRED_MESSAGE = "Token is expired";


    /**
     * Overrides the logout method from the LogoutHandler interface.
     * It retrieves the token from the request, finds the corresponding token in the repository,
     * invalidates it, and clears the authentication context. If the token is not found, it sends an
     * unauthorized response.
     *
     * @param request        the HTTP request containing the logout information
     * @param response       the HTTP response to be sent back to the client
     * @param authentication the Authentication object representing the user being logged out
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = securityFilter.recoverTokenFromRequest(request);

        Token storedToken = findTokenByValue(token);

        if (storedToken != null) {
            invalidateToken(storedToken);
            SecurityContextHolder.clearContext();
            log.info("[LOGOUT_SUCCESS] User logged out successfully. Token invalidated: {}", token);
        } else {
            log.error("[TOKEN_EXPIRED] Token not found during logout: {}", token);
            try {
                sendUnauthorizedResponse(response);
            } catch (IOException e) {
                log.error("[LOGOUT_ERROR] Error sending unauthorized response", e);
                throw new TokenGenerationException("Error sending unauthorized response", e);
            }
        }
    }

    public Token findTokenByValue(String tokenValue) {
        Token token = tokenRepository.findByTokenValue(tokenValue).orElse(null);
        if (token == null) {
            log.warn("[TOKEN_EXPIRED] Token not found in repository during logout for value: {}", tokenValue);
        }
        return token;
    }

    public void invalidateToken(Token token) {
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    public void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(TOKEN_EXPIRED_MESSAGE);
    }
}

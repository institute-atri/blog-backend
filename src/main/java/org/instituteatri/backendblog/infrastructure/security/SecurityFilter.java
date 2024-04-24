package org.instituteatri.backendblog.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenInvalidException;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    final TokenService tokenService;
    final TokenRepository tokenRepository;
    final UserRepository userRepository;


    /**
     * Spring calls this method for each request.
     * It attempts to recover the JWT token from the
     * request's authorization header, and if present, it attempts to validate and authenticate the token.
     * If the token is valid, the user's authentication is set in the security context.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the Spring filter chain
     * @throws ServletException if an error occurs during the filter chain processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverTokenFromRequest(request);

        if (token != null) {
            handleAuthentication(token, response);
        }

        filterChain.doFilter(request, response);
    }

    public String recoverTokenFromRequest(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }


    /**
     * This method attempts to validate and authenticate the token.
     * If the token is valid, the user's authentication is set in the security context.
     *
     * @param token    the JWT token to be validated
     * @param response the outgoing HTTP response
     * @throws IOException      if an I/O error occurs
     */
    private void handleAuthentication(String token, HttpServletResponse response)
            throws IOException {

        try {
            String email = tokenService.isValidateToken(token);
            UserDetails userDetails = getUserDetailsByEmail(email);
            boolean isTokenValid = isTokenValid(token);

            if (userDetails != null && isTokenValid) {
                setAuthenticationInSecurityContext(userDetails);
            }
        } catch (TokenInvalidException e) {
            handleInvalidToken(response, e);
        }
    }

    private UserDetails getUserDetailsByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    private void setAuthenticationInSecurityContext(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleInvalidToken(HttpServletResponse response, TokenInvalidException e)
            throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(e.getMessage());
    }
}

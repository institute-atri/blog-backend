package org.instituteatri.backendblog.service.strategy.implstrategy;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.domain.token.TokenType;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.strategy.interfaces.AuthenticationTokenManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenManagerImpl implements AuthenticationTokenManager {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    /**
     * This `generateTokenResponse` method is responsible for generating access and refresh tokens
     * for a specific user. First, it calls the `tokenService` service to generate an access token
     * and a refresh token for the user provided. It then clears all existing tokens for the user,
     * ensuring that only the newly generated tokens are valid. After clearing the old tokens,
     * the method saves the new tokens in the database associated with the user,
     * ensuring that they are correctly linked to the user.
     * Finally, it returns a `ResponseDTO` object containing the values of the newly generated access
     * and update tokens, which can be sent back to the client as part of a response to a successful
     * authentication request. This function is crucial for ensuring that the user receives the tokens
     * needed to securely access the application's protected resources.
     * Generates access and refresh tokens for a specific user.
     *
     * @param user The user for whom the tokens are generated.
     * @return A ResponseDTO object containing the values of the newly generated access and refresh tokens.
     */
    @Override
    public TokenResponseDTO generateTokenResponse(User user) {
        try {
            String accessToken = tokenService.generateAccessToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);

            clearTokens(user.getId());
            Token accessTokenToken = saveUserToken(user, accessToken);
            Token refreshTokenToken = saveUserToken(user, refreshToken);

            user.getTokens().clear();
            user.getTokens().add(accessTokenToken);
            user.getTokens().add(refreshTokenToken);
            userRepository.save(user);

            return new TokenResponseDTO(accessTokenToken.getTokenValue(), refreshTokenToken.getTokenValue());
        } catch (TokenGenerationException e) {
            throw new TokenGenerationException("Failed to generate access or refresh token", e);
        }
    }

    public void clearTokens(String userId) {
        var tokens = tokenRepository.findAllByUserId(userId);
        if (!tokens.isEmpty()) {
            tokenRepository.deleteAll(tokens);
        }
    }

    public Token saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        try {
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user token", e);
        }
    }

    @Override
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            try {
                tokenRepository.saveAll(validUserTokens);
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to revoke user tokens", e);
            }
        }
    }
}

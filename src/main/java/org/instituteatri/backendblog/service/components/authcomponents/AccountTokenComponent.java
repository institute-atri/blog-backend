package org.instituteatri.backendblog.service.components.authcomponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.domain.token.TokenType;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountTokenComponent {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public TokenResponseDTO generateTokenResponse(User user) {
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
    }

    private void clearTokens(String userId) {
        var tokens = tokenRepository.findAllByUserId(userId);
        if (!tokens.isEmpty()) {
            tokenRepository.deleteAll(tokens);
        }
    }

    private Token saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        return tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }
}

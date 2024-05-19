package org.instituteatri.backendblog.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.entities.UserRole;
import org.instituteatri.backendblog.dto.request.LoginRequestDTO;
import org.instituteatri.backendblog.dto.request.RefreshTokenRequestDTO;
import org.instituteatri.backendblog.dto.request.RegisterRequestDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.AccountLockedException;
import org.instituteatri.backendblog.infrastructure.exceptions.EmailAlreadyExistsException;
import org.instituteatri.backendblog.infrastructure.exceptions.TooManyRequestsException;
import org.instituteatri.backendblog.infrastructure.security.IPBlockingService;
import org.instituteatri.backendblog.infrastructure.security.IPResolverService;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.infrastructure.security.UserCreationRateLimiterService;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.strategy.interfaces.AccountLoginManager;
import org.instituteatri.backendblog.service.strategy.interfaces.AuthenticationTokenManager;
import org.instituteatri.backendblog.service.strategy.interfaces.PasswordValidationStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final IPBlockingService ipBlockingService;
    private final IPResolverService ipResolverService;
    private final AuthenticationTokenManager authTokenManager;
    private final AccountLoginManager accountLoginManager;
    private final PasswordValidationStrategy passwordValidationStrategy;
    private final UserCreationRateLimiterService userCreationRateLimiterService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public ResponseEntity<TokenResponseDTO> processLogin(LoginRequestDTO authDto, AuthenticationManager authManager) {

        String ipAddress = ipResolverService.getRealClientIP();
        log.debug("Processing login request from IP address: {}", ipAddress);

        checkIPBlock(ipAddress);

        try {
            var authResult = accountLoginManager.authenticateUser(authDto, authManager);
            var user = (User) authResult.getPrincipal();

            accountLoginManager.handleSuccessfulLogin(user);
            authTokenManager.revokeAllUserTokens(user);

            log.info("User login successful: {}", user.getEmail());
            return ResponseEntity.ok(authTokenManager.generateTokenResponse(user));

        } catch (LockedException e) {
            log.warn("User account locked: {}", authDto.email());
            throw new AccountLockedException();

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt with email: {}", authDto.email());
            return accountLoginManager.handleBadCredentials(authDto.email());
        }
    }

    public ResponseEntity<TokenResponseDTO> processRegister(RegisterRequestDTO registerRequestDTO) {

        String ipAddress = ipResolverService.getRealClientIP();
        log.debug("Processing registration request from IP address: {}", ipAddress);
        checkIPBlock(ipAddress);

        if (!userCreationRateLimiterService.allowUserCreation(ipAddress)) {
            log.warn("User creation rate limit exceeded for IP address: {}", ipAddress);
            throw new TooManyRequestsException(ipAddress);
        }

        if (isEmailExists(registerRequestDTO.email())) {
            log.warn("Email already exists: {}", registerRequestDTO.email());
            throw new EmailAlreadyExistsException();
        }

        passwordValidationStrategy.validate(registerRequestDTO.password(), registerRequestDTO.confirmPassword());

        User newUser = buildUserFromRegistrationDto(registerRequestDTO);
        User savedUser = userRepository.insert(newUser);
        String baseUri = "http://localhost:8080";

        URI uri = UriComponentsBuilder.fromUriString(baseUri)
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        log.info("User registered successfully: {}", savedUser.getEmail());
        return ResponseEntity.created(uri).body(authTokenManager.generateTokenResponse(savedUser));
    }

    public ResponseEntity<TokenResponseDTO> processRefreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        try {
            String userEmail = tokenService.validateToken(refreshTokenRequestDTO.refreshToken());
            UserDetails userDetails = loadUserByUsername(userEmail);

            var user = (User) userDetails;
            authTokenManager.revokeAllUserTokens(user);

            log.info("Token refreshed successfully for user: {}", user.getEmail());
            return ResponseEntity.ok(authTokenManager.generateTokenResponse(user));

        } catch (Exception e) {
            log.error("Error processing token refresh request: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isEmailExists(String email) {
        return loadUserByUsername(email) != null;
    }

    private User buildUserFromRegistrationDto(RegisterRequestDTO registerRequestDTO) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDTO.password());
        return new User(
                registerRequestDTO.name(),
                registerRequestDTO.lastName(),
                registerRequestDTO.phoneNumber(),
                registerRequestDTO.bio(),
                registerRequestDTO.email(),
                encryptedPassword,
                true,
                UserRole.USER
        );
    }

    protected void checkIPBlock(String ipAddress) {
        if (ipBlockingService.isBlocked(ipAddress)) {
            log.warn("Request blocked from IP address: {}", ipAddress);
            throw new TooManyRequestsException(ipAddress);
        }
    }
}

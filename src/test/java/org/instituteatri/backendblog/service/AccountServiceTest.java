package org.instituteatri.backendblog.service;

import org.instituteatri.backendblog.domain.entities.User;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private IPBlockingService ipBlockingService;

    @Mock
    private IPResolverService ipResolverService;

    @Mock
    private AuthenticationTokenManager authTokenManager;

    @Mock
    private AccountLoginManager accountLoginManager;

    @Mock
    private PasswordValidationStrategy passwordValidationStrategy;

    @Mock
    private UserCreationRateLimiterService userCreationRateLimiterService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private Authentication authResult;

    private final String ipAddress = "127.0.0.1";
    private final LoginRequestDTO authDto = new LoginRequestDTO("user@example.com", "password");
    private final RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
            "Name",
            "LastName",
            "1234567890",
            "Bio",
            "registeruser@example.com",
            "password",
            "password");

    @Nested
    @DisplayName("Test processLogin method")
    class testLoginMethod {
        @Test
        @DisplayName("processLogin should return token response when login is successful")
        void processLogin_ShouldReturnTokenResponse_WhenLoginIsSuccessful() {
            // Arrange
            User userLogin = new User();
            userLogin.setEmail("user@example.com");
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(accountLoginManager.authenticateUser(authDto, authManager)).thenReturn(authResult);
            when(authResult.getPrincipal()).thenReturn(userLogin);
            TokenResponseDTO tokenResponse = new TokenResponseDTO("token", "refreshToken");
            when(authTokenManager.generateTokenResponse(userLogin)).thenReturn(tokenResponse);

            // Act
            ResponseEntity<TokenResponseDTO> response = accountService.processLogin(authDto, authManager);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(tokenResponse, response.getBody());
            verify(authTokenManager).revokeAllUserTokens(userLogin);
            verify(accountLoginManager).handleSuccessfulLogin(userLogin);
        }

        @Test
        @DisplayName("processLogin should throw AccountLockedException when user account is locked")
        void processLogin_ShouldThrowAccountLockedException_WhenUserAccountIsLocked() {
            // Arrange
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(accountLoginManager.authenticateUser(authDto, authManager)).thenThrow(LockedException.class);

            // Act & Assert
            assertThrows(AccountLockedException.class, () -> accountService.processLogin(authDto, authManager));
            verify(authTokenManager, never()).generateTokenResponse(any(User.class));
        }

        @Test
        @DisplayName("processLogin should handle bad credentials and return appropriate response")
        void processLogin_ShouldHandleBadCredentials_WhenBadCredentialsProvided() {
            // Arrange
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(accountLoginManager.authenticateUser(authDto, authManager)).thenThrow(BadCredentialsException.class);
            ResponseEntity<TokenResponseDTO> badCredentialsResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            when(accountLoginManager.handleBadCredentials(authDto.email())).thenReturn(badCredentialsResponse);

            // Act
            ResponseEntity<TokenResponseDTO> response = accountService.processLogin(authDto, authManager);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authTokenManager, never()).generateTokenResponse(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test register method")
    class testRegisterMethod {
        @Test
        @DisplayName("processRegister should create new user and return created response with token")
        void processRegister_ShouldCreateNewUser_WhenRequestIsValid() {
            // Arrange
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(userCreationRateLimiterService.allowUserCreation(ipAddress)).thenReturn(true);
            when(userRepository.findByEmail(registerRequestDTO.email())).thenReturn(null);
            doNothing().when(passwordValidationStrategy).validate(registerRequestDTO.password(), registerRequestDTO.confirmPassword());

            User newUser = new User();
            newUser.setEmail(registerRequestDTO.email());
            when(userRepository.insert(any(User.class))).thenReturn(newUser);

            TokenResponseDTO tokenResponse = new TokenResponseDTO("token", "refreshToken");
            when(authTokenManager.generateTokenResponse(newUser)).thenReturn(tokenResponse);

            URI location = UriComponentsBuilder
                    .fromUriString("http://localhost:8080")
                    .path("/{id}")
                    .buildAndExpand(newUser.getId())
                    .toUri();

            // Act
            ResponseEntity<TokenResponseDTO> response = accountService.processRegister(registerRequestDTO);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(location, response.getHeaders().getLocation());
            assertEquals(tokenResponse, response.getBody());
            TokenResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
        }

        @Test
        @DisplayName("processRegister should throw EmailAlreadyExistsException when email already exists")
        void processRegister_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {
            // Arrange
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(userCreationRateLimiterService.allowUserCreation(ipAddress)).thenReturn(true);
            when(userRepository.findByEmail(registerRequestDTO.email())).thenReturn(new User());

            // Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () -> accountService.processRegister(registerRequestDTO));
        }

        @Test
        @DisplayName("processRegister should throw TooManyRequestsException when user creation rate limit is exceeded")
        void processRegister_ShouldThrowTooManyRequestsException_WhenRateLimitExceeded() {
            // Arrange
            when(ipResolverService.getRealClientIP()).thenReturn(ipAddress);
            when(userCreationRateLimiterService.allowUserCreation(ipAddress)).thenReturn(false);

            // Act & Assert
            assertThrows(TooManyRequestsException.class, () -> accountService.processRegister(registerRequestDTO));
        }

    }

    @Nested
    @DisplayName("Test refresh token method")
    class testRefreshTokenMethod {
        @Test
        @DisplayName("processRefreshToken should return new token response when refresh token is valid")
        void processRefreshToken_ShouldReturnNewTokenResponse_WhenRefreshTokenIsValid() {
            // Arrange
            String validRefreshToken = "valid-refresh-token";
            String userEmail = "validuser@example.com";

            RefreshTokenRequestDTO validRefreshTokenRequestDTO = new RefreshTokenRequestDTO(validRefreshToken);

            User validUser = new User();
            validUser.setEmail(userEmail);
            when(tokenService.validateToken(validRefreshToken)).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(validUser);

            TokenResponseDTO tokenResponse = new TokenResponseDTO("new-token", "refreshToken");
            when(authTokenManager.generateTokenResponse(validUser)).thenReturn(tokenResponse);

            // Act
            ResponseEntity<TokenResponseDTO> response = accountService.processRefreshToken(validRefreshTokenRequestDTO);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(tokenResponse, response.getBody());
            verify(authTokenManager).revokeAllUserTokens(validUser);
        }

        @Test
        @DisplayName("processRefreshToken should return internal server error when an exception occurs")
        void processRefreshToken_ShouldReturnInternalServerError_WhenExceptionOccurs() {
            // Arrange
            String invalidRefreshToken = "invalid-refresh-token";
            RefreshTokenRequestDTO invalidRefreshTokenRequestDTO = new RefreshTokenRequestDTO(invalidRefreshToken);
            when(tokenService.validateToken(invalidRefreshToken)).thenThrow(RuntimeException.class);

            // Act
            ResponseEntity<TokenResponseDTO> response = accountService.processRefreshToken(invalidRefreshTokenRequestDTO);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Test check IP block method")
    class testCheckIPBlockMethod {
        @Test
        @DisplayName("checkIPBlock should throw TooManyRequestsException when IP is blocked")
        void checkIPBlock_ShouldThrowTooManyRequestsException_WhenIPisBlocked() {
            // Arrange
            String blockedIP = "127.0.0.1";
            when(ipBlockingService.isBlocked(blockedIP)).thenReturn(true);

            // Act & Assert
            assertThrows(TooManyRequestsException.class, () -> accountService.checkIPBlock(blockedIP));
        }

        @Test
        @DisplayName("checkIPBlock should not throw exception when IP is not blocked")
        void checkIPBlock_ShouldNotThrowException_WhenIPisNotBlocked() {
            // Arrange
            String unblockedIP = "192.168.1.1";
            when(ipBlockingService.isBlocked(unblockedIP)).thenReturn(false);

            // Act & Assert
            assertDoesNotThrow(() -> accountService.checkIPBlock(unblockedIP));
        }
    }
}
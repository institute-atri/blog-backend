package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.LoginRequestDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomAuthenticationException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountLoginManagerImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private User user;

    @InjectMocks
    private AccountLoginManagerImpl accountLoginManager;


    @Nested
    class testAuthenticateUserMethod {

        @Test
        void testAuthenticateUser_shouldThrowException_whenAuthenticationFails() {
            // Arrange
            LoginRequestDTO authDto = new LoginRequestDTO("user@example.com", "password");

            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act and Assert
            assertThrows(BadCredentialsException.class, () ->
                    accountLoginManager.authenticateUser(authDto, authManager));
        }

        @Test
        void testAuthenticateUser_shouldReturnAuthentication_whenAuthenticationSucceeds() {
            // Arrange
            LoginRequestDTO authDto = new LoginRequestDTO("user@example.com", "password");
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

            Authentication expectedAuthentication = new UsernamePasswordAuthenticationToken(authDto.email(), authDto.password(), authorities);
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(expectedAuthentication);

            // Act
            Authentication result = accountLoginManager.authenticateUser(authDto, authManager);

            // Assert
            assertEquals(expectedAuthentication, result);
        }
    }

    @Nested
    class testHandleSuccessfulLoginMethod {

        @Test
        void handleSuccessfulLoginWhenUserIsActive() {
            // Arrange
            when(user.isActive()).thenReturn(true);

            // Act
            accountLoginManager.handleSuccessfulLogin(user);

            // Assert
            verify(user).setFailedLoginAttempts(0);
            verify(userRepository).save(user);
        }

        @Test
        void handleSuccessfulLoginWhenUserIsNotActiveAndHasLessThan4FailedAttempts() {
            // Arrange
            when(user.isActive()).thenReturn(false);
            when(user.getFailedLoginAttempts()).thenReturn(2);

            // Act and Assert
            assertThrows(LockedException.class, () -> accountLoginManager.handleSuccessfulLogin(user));

            verify(user).setFailedLoginAttempts(3);
            verify(userRepository).save(user);
        }

        @Test
        void handleSuccessfulLoginWhenUserIsNotActiveAndHasExactly4FailedAttempts() {
            // Arrange
            when(user.isActive()).thenReturn(false);
            when(user.getFailedLoginAttempts()).thenReturn(4);

            // Act and Assert
            assertThrows(LockedException.class, () -> accountLoginManager.handleSuccessfulLogin(user));

            verify(user).setFailedLoginAttempts(5);
            verify(user).lockAccountForHours();
            verify(userRepository, times(2)).save(user);
        }

        @Test
        void handleSuccessfulLoginWhenUserIsNotActiveAndHasMoreThan4FailedAttempts() {
            // Arrange
            when(user.isActive()).thenReturn(false);
            when(user.getFailedLoginAttempts()).thenReturn(5);

            // Act and Assert
            assertThrows(LockedException.class, () -> accountLoginManager.handleSuccessfulLogin(user));

            verify(user).setFailedLoginAttempts(6);
            verify(user).lockAccountForHours();
            verify(userRepository, times(2)).save(user);
        }
    }

    @Nested
    class testHandleBadCredentialsMethod {
        @Test
        void handleBadCredentialsWhenUserIsFoundAndHasLessThan4FailedAttempts() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(user.getFailedLoginAttempts()).thenReturn(2);

            // Act and Assert
            assertThrows(CustomAuthenticationException.class, () -> accountLoginManager.handleBadCredentials("user@example.com"));

            verify(user).setFailedLoginAttempts(3);
            verify(userRepository).save(user);
        }

        @Test
        void handleBadCredentialsWhenUserIsFoundAndHasExactly4FailedAttempts() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(user.getFailedLoginAttempts()).thenReturn(4);

            // Act and Assert
            assertThrows(CustomAuthenticationException.class, () -> accountLoginManager.handleBadCredentials("user@example.com"));

            verify(user).setFailedLoginAttempts(5);
            verify(user).lockAccountForHours();
            verify(userRepository, times(2)).save(user);
        }

        @Test
        void handleBadCredentialsWhenUserIsNotFound() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(null);

            // Act
            assertThrows(CustomAuthenticationException.class, () -> accountLoginManager.handleBadCredentials("user@example.com"));

            // Assert
            verify(userRepository, never()).save(any(User.class));
        }
    }
}

package org.instituteatri.backendblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instituteatri.backendblog.dto.request.LoginRequestDTO;
import org.instituteatri.backendblog.dto.request.RefreshTokenRequestDTO;
import org.instituteatri.backendblog.dto.request.RegisterRequestDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.service.AccountService;
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
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccountService accountService;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Authentication authentication;

    @InjectMocks
    private AccountController accountController;

    @Nested
    class login {

        @Test
        @DisplayName("Should return login success")
        void shouldReturnLoginSuccess() {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO("test@localhost.com", "@Test123k+");
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO("token", "refreshToken");
            when(accountService.processLogin(loginRequest, authenticationManager)).thenReturn(
                    ResponseEntity.ok(tokenResponseDTO));

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.login(loginRequest);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(tokenResponseDTO);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accountService).processLogin(loginRequest, authenticationManager);
        }

        @Test
        @DisplayName("Should return login failure")
        void shouldReturnLoginFailure() {
            // Arrange
            LoginRequestDTO loginRequest = new LoginRequestDTO("test@localhost.com", "@Test123k+");
            when(accountService.processLogin(loginRequest, authenticationManager)).thenReturn(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.login(loginRequest);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            verify(accountService).processLogin(loginRequest, authenticationManager);
        }
    }

    @Nested
    class register {

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "test",
                "test",
                "12314556",
                "test",
                "test@localhost.com",
                "@Test123k+",
                "@Test123k+");

        @Test
        @DisplayName("Should return registration success")
        void shouldReturnRegistrationSuccess() {
            // Arrange
            when(accountService.processRegister(registerRequest)).thenReturn(
                    ResponseEntity.status(HttpStatus.OK).build());

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.register(registerRequest);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accountService).processRegister(registerRequest);
        }

        @Test
        @DisplayName("Should return registration failure")
        void shouldReturnRegistrationFailure() {
            // Arrange
            when(accountService.processRegister(registerRequest)).thenReturn(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.register(registerRequest);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(accountService).processRegister(registerRequest);
        }
    }

    @Nested
    class logout {

        @Test
        @DisplayName("Should return logout success")
        void shouldReturnLogoutSuccess() {
            // Act
            ResponseEntity<Void> responseEntity = accountController.logout(request, response, authentication);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class refreshToken {
        @Test
        @DisplayName("Should return refreshToken success")
        void shouldReturnRefreshTokenSuccess() {
            // Arrange
            RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO("refreshToken");
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO("token", "refreshToken");
            when(accountService.processRefreshToken(refreshTokenRequest)).thenReturn(
                    ResponseEntity.ok(tokenResponseDTO));

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.refreshToken(refreshTokenRequest);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(tokenResponseDTO);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accountService).processRefreshToken(refreshTokenRequest);
        }

        @Test
        @DisplayName("Should return refreshToken failure")
        void shouldReturnRefreshTokenFailure() {
            // Arrange
            RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO("refreshToken");
            when(accountService.processRefreshToken(refreshTokenRequest))
                    .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = accountController.refreshToken(refreshTokenRequest);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            verify(accountService).processRefreshToken(refreshTokenRequest);
        }
    }
}
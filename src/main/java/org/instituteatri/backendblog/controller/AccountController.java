package org.instituteatri.backendblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.dto.request.LoginRequestDTO;
import org.instituteatri.backendblog.dto.request.RefreshTokenRequestDTO;
import org.instituteatri.backendblog.dto.request.RegisterRequestDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AccountController {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    @Operation(
            method = "POST",
            description = "Endpoint for authentication. If a user enters the wrong password more than four times, their account will be locked. Upon successful login, returns a token and email.",
            summary = "Authenticate user by verifying credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"token\": [\"string\"]," +
                                            "\"email\": [\"string\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "404", description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Invalid username or password.\"}"
                            ))),

            @ApiResponse(responseCode = "403", description = "Forbidden.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Account is locked.\"}"
                            ))),
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO authDto) {
        return accountService.processLogin(authDto, authenticationManager);
    }


    @Operation(
            method = "POST",
            summary = "Register a new user.",
            description = "Endpoint for user registration." +
                    " Accepts " +
                    "'name' (string), " +
                    "'lastName' (string)" +
                    " 'phoneNumber' (string)," +
                    " 'email' (string), and" +
                    " 'password' (string)." +
                    " 'confirmPassword' (string)." +
                    " Upon successful registration, returns a token and email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"token\": [\"string\"]," +
                                            "\"email\": [\"string\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name must be between 5 and 30 characters.\"]," +
                                            "\"lastName\": [\"Last name is required.\", \"Last name must be between 5 and 30 characters.\"]," +
                                            "\"phoneNumber\": [\"Phone number is required.\", \"Phone number must be less than 11 characters.\", \"Phone number must contain only digits.\"]," +
                                            "\"email\": [\"Email is required.\", \"Invalid email format.\", \"Email must be between 10 and 30 characters.\"]," +
                                            "\"password\": [\"Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.\", \"Password must be between 10 and 30 characters.\", \"Password is required.\"]," +
                                            "\"confirmPassword\": [\"Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.\", \"Password must be between 10 and 30 characters.\", \"Password is required.\"]," +
                                            "\"message\": [\"Passwords do not match.\"]" +
                                            "}"
                            ))),
            @ApiResponse(responseCode = "409", description = "Conflicts.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"E-mail not available.\"}"
                            )))
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
        return accountService.processRegister(registerRequestDTO);
    }

    @Operation(
            method = "POST",
            summary = "Logout the current user.",
            description = "Endpoint to logout the currently authenticated user. " +
                    "Invalidates the current session and clears the security context.",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Refresh the access token using a refresh token.",
            description = "Endpoint to refresh the access token using a valid refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO tokenDTO) {
        return accountService.processRefreshToken(tokenDTO);
    }
}
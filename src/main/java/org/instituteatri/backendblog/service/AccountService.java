package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.entities.UserRole;
import org.instituteatri.backendblog.dtos.AuthenticationDTO;
import org.instituteatri.backendblog.dtos.RegisterDTO;
import org.instituteatri.backendblog.dtos.ResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.EmailAlreadyExistsException;
import org.instituteatri.backendblog.infrastructure.exceptions.PasswordsNotMatchException;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.components.authComponents.AccountLoginComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AccountLoginComponent accountLoginComponent;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public ResponseEntity<ResponseDTO> processLogin(AuthenticationDTO authDto, AuthenticationManager authManager) {
        try {
            var authResult = accountLoginComponent.authenticateUserComponent(authDto, authManager);
            var user = (User) authResult.getPrincipal();

            accountLoginComponent.handleSuccessfulLoginComponent(user);

            var token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(token, user.getEmail()));

        } catch (LockedException e) {
            return accountLoginComponent.handleLockedAccountComponent();

        } catch (BadCredentialsException e) {
            return accountLoginComponent.handleBadCredentialsComponent(authDto.email());
        }
    }


    public ResponseEntity<ResponseDTO> processRegister(RegisterDTO registerDTO) {
        if (isEmailExists(registerDTO.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (!registerDTO.password().equals(registerDTO.confirmPassword())) {
            throw new PasswordsNotMatchException();
        }
        User newUser = createUser(registerDTO);
        User savedUser = userRepository.insert(newUser);
        URI uri = buildUserUri(savedUser);

        String token = tokenService.generateToken(savedUser);

        return ResponseEntity.created(uri).body(new ResponseDTO(token, savedUser.getEmail()));
    }

    private boolean isEmailExists(String email) {
        return loadUserByUsername(email) != null;
    }

    private User createUser(RegisterDTO registerDTO) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
        return new User(
                registerDTO.name(),
                registerDTO.lastName(),
                registerDTO.phoneNumber(),
                registerDTO.bio(),
                registerDTO.email(),
                encryptedPassword,
                true,
                UserRole.USER
        );
    }

    private URI buildUserUri(User user) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
    }
}

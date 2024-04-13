package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.AuthenticationDTO;
import org.instituteatri.backendblog.dtos.ResponseDTO;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomAuthenticationException;
import org.instituteatri.backendblog.infrastructure.exceptions.EmailAlreadyExistsException;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public ResponseEntity<?> processLogin(AuthenticationDTO authDto, AuthenticationManager authManager) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(authDto.email(), authDto.password());
            var auth = authManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new ResponseDTO(token));

        } catch (AuthenticationException e) {
            throw new CustomAuthenticationException();
        }
    }


    public ResponseEntity<?> processRegister(UserDTO userDto) {
        if (emailExists(userDto.email())) {
            throw new EmailAlreadyExistsException();
        }
        User newUser = createUser(userDto);
        User savedUser = userRepository.insert(newUser);
        URI uri = buildUserUri(savedUser);

        return ResponseEntity.created(uri).build();
    }

    private boolean emailExists(String email) {
        return loadUserByUsername(email) != null;
    }

    private User createUser(UserDTO userDto) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(userDto.password());
        return new User(
                userDto.name(),
                userDto.lastName(),
                userDto.phoneNumber(),
                userDto.bio(),
                userDto.email(),
                encryptedPassword,
                true,
                userDto.role());
    }

    private URI buildUserUri(User user) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
    }
}

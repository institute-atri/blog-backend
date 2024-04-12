package org.instituteatri.backendblog.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.infrastructure.security.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurations {

    final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/user/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/user/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/user/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/v1/user/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/user/{id}/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/post/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/post/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/post/create").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/v1/post/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/v1/post/{id}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


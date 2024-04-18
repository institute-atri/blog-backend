package org.instituteatri.backendblog.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.infrastructure.security.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    final AuthSecurityConfig authSecurityConfig;
    final UserSecurityConfig userSecurityConfig;
    final PostSecurityConfig postSecurityConfig;
    final TagSecurityConfig tagSecurityConfig;
    final CategorySecurityConfig categorySecurityConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        applyAuthSecurityConfig(httpSecurity);
        applyUserSecurityConfig(httpSecurity);
        applyPostSecurityConfig(httpSecurity);
        applyTagSecurityConfig(httpSecurity);
        applyCategorySecurityConfig(httpSecurity);

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        )
                        .permitAll()
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


    private void applyAuthSecurityConfig(HttpSecurity http) throws Exception {
        authSecurityConfig.configure(http);
    }

    private void applyUserSecurityConfig(HttpSecurity http) throws Exception {
        userSecurityConfig.configure(http);
    }

    private void applyPostSecurityConfig(HttpSecurity http) throws Exception {
        postSecurityConfig.configure(http);
    }

    private void applyTagSecurityConfig(HttpSecurity http) throws Exception {
        tagSecurityConfig.configure(http);
    }

    private void applyCategorySecurityConfig(HttpSecurity http) throws Exception {
        categorySecurityConfig.configure(http);
    }
}


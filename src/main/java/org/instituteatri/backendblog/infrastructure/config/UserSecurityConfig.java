package org.instituteatri.backendblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;


@Configuration
public class UserSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/users").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.GET, "/v1/users/find/{id}").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.DELETE, "/v1/users/delete/{id}").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.PUT, "/v1/users/update/{id}").hasAnyRole(ADMIN_ROLE, USER_ROLE)
                .requestMatchers(HttpMethod.GET, "/v1/users/posts/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/users/change-password").hasAnyRole(ADMIN_ROLE, USER_ROLE)
        );
    }
}

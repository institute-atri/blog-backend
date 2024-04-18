package org.instituteatri.backendblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;


@Configuration
class UserSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/user/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/v1/user/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/v1/user/{id}").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/v1/user/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/v1/user/{id}/posts").permitAll()
        );
    }
}

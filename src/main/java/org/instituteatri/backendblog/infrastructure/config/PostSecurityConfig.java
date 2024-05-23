package org.instituteatri.backendblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class PostSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/posts/find/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/posts/create").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.PUT, "/v1/posts/update/{id}").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.DELETE, "/v1/posts/delete/{id}").hasRole(ADMIN_ROLE)
        );
    }
}

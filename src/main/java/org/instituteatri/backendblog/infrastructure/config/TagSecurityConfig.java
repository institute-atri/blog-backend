package org.instituteatri.backendblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;


@Configuration
public class TagSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String ADMIN_ROLE = "ADMIN";
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/tags").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/tags/find/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/tags/posts/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/tags/create").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.PUT, "/v1/tags/update/{id}").hasRole(ADMIN_ROLE)
                .requestMatchers(HttpMethod.DELETE, "/v1/tags/delete/{id}").hasRole(ADMIN_ROLE)
        );
    }
}
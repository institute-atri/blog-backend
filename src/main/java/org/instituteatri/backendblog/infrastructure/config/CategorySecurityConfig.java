package org.instituteatri.backendblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class CategorySecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/category/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/category/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/category/{id}/posts").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/category/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/v1/category/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/category/{id}").hasRole("ADMIN")
        );
    }
}
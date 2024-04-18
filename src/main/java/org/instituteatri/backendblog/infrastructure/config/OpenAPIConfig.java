package org.instituteatri.backendblog.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${blog.openapi.dev-url}")
    private String devUrl;
    @Value("${blog.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");
        openAPI.setServers(List.of(devServer, prodServer));

        Contact contact = new Contact();
        contact.setEmail("contactorgatri@gmail.com");
        contact.setName("ATRI");
        contact.setUrl("https://www.orgatri.com");
 
        License mitLicense = new License().name("MIT License").url("https://github.com/institute-atri/blog-backend?tab=MIT-1-ov-file");
        Info info = new Info()
                .title("Blog backend")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for managing blog.")
                .termsOfService("https://www.orgatri.com/terms")
                .license(mitLicense);
        openAPI.setInfo(info);
        openAPI.components(new Components()
                        .addSecuritySchemes("JWTToken", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWTToken")
                );
        return openAPI;
    }
}
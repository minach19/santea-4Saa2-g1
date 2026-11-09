package com.santea.admin;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI santeaOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("SANTEA - API d'administration")
                        .version("v1")
                        .description("Authentification administrateur et gestion centralisée des comptes."))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}

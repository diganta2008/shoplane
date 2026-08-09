package com.shoplane.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shoplaneOpenApi() {
        var bearer = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("ShopLane REST API")
                        .version("v1")
                        .description("Spring Boot backend for the ShopLane e-commerce demo. " +
                                "Called directly by the static client on :5500."))
                .components(new Components().addSecuritySchemes("bearer-jwt", bearer))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

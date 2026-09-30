package com.rodrigo.portfolio.gerencimento_clientes.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gerenciamento de Clientes")
                        .version("1.0.0")
                        .description("Sistema de Gestão de Clientes - Portfólio")
                        .contact(new Contact()
                                .name("Rodrigo")
                                .email("seu.email@email.com")
                                .url("https://github.com/seu-usuario"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor Local"));
    }
}
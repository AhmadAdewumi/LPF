package com.ahmad.ProductFinder.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI defineOpenApi(){
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Ahmad Adewumi");
        myContact.setEmail("ahmadadewumi@gmail.com");

        Info info = new Info()
                .title("Product finder application API")
                .version("1.0")
                .description("This API exposes endpoint for the product finder application")
                .contact(myContact);

        return new OpenAPI().info(info).servers(List.of(server));
    }
}

package com.example.abckafka;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "ABC Kafka CRUD API",
        version = "1.0.0",
        description = "API REST para operaciones ABC (Alta-Baja-Cambio) de productos "
                    + "con eventos Kafka y persistencia en PostgreSQL",
        contact = @Contact(name = "Equipo de Desarrollo", email = "dev@example.com")
    )
)
public class AbcKafkaCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbcKafkaCrudApplication.class, args);
    }
}

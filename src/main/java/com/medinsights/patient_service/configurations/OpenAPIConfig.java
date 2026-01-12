package com.medinsights.patient_service.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedInsights Patient Service API",
                version = "1.0.0",
                description = "Centralized patient management system for administrative and medical information",
                contact = @Contact(
                        name = "MedInsights Team",
                        email = "support@medinsights.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.medinsights.com", description = "Production Server (via Kong Gateway)")
        }
)
public class OpenAPIConfig {
}

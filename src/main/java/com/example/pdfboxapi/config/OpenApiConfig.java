package com.example.pdfboxapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation
 * 
 * This class configures the OpenAPI documentation for the PDFBox API Services.
 * It provides metadata about the API including title, description, version,
 * contact information, and license details.
 * 
 * @author Manus
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation
     * 
     * @return OpenAPI object with API metadata
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PDFBox API Services")
                        .description("REST API services for PDF operations using Apache PDFBox")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PDFBox API Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

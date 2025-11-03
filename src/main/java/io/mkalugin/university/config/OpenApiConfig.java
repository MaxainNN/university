package io.mkalugin.university.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenApi (Swagger).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создание и настройка объекта {@link OpenAPI}, с
     * описанием API.
     *
     * @return экземпляр {@link OpenAPI} для генерации Swagger UI
     * и спецификации OpenAPI.
     */
    @Bean
    public OpenAPI universityOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                .title("Digital Calendar API")
                .description("API для системы электронного календаря университета")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Максим Калугин")
                        .email("imenolys23@gmail.com"))
                .license(new License()
                         .name("Apache 2.0")
                         .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

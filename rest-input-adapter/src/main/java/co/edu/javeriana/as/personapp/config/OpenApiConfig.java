package co.edu.javeriana.as.personapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Información general de la API que muestra Swagger UI en
 * {@code http://localhost:3000/swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Define el título, la versión, la descripción y la licencia de la especificación
	 * OpenAPI.
	 *
	 * @return definición OpenAPI de la aplicación
	 */
	@Bean
	public OpenAPI personAppOpenApi() {
		return new OpenAPI().info(new Info().title("PersonApp API").version("1.0.0")
				.description("CRUD de personas, profesiones, telefonos y estudios sobre arquitectura hexagonal. "
						+ "Cada operacion recibe el motor de persistencia (MARIA o MONGO) y lo cambia en caliente, "
						+ "sin reiniciar la aplicacion.")
				.license(new License().name("Apache License, Version 2.0")
						.url("https://www.apache.org/licenses/LICENSE-2.0")));
	}
}

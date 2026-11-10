package co.edu.javeriana.as.personapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Aplicación Spring Boot del adaptador de entrada REST. Publica la API en el
 * puerto 3000 y la documentación Swagger en {@code /swagger-ui.html}.
 */
@Slf4j
@SpringBootApplication
public class PersonAppRestApi {

	/**
	 * Arranca la aplicación.
	 *
	 * @param args argumentos de línea de comandos; permiten sobrescribir propiedades,
	 *             por ejemplo {@code --server.port=8080}
	 */
	public static void main(String[] args) {
		log.info("Starting PersonAppRestApi ...");
		SpringApplication.run(PersonAppRestApi.class, args);
		log.info("Started PersonAppRestApi OK");
	}

}

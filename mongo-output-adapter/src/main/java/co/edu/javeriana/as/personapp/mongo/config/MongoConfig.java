package co.edu.javeriana.as.personapp.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * Configuracion propia del adaptador MongoDB.
 *
 * Por defecto Spring Data convierte un LocalDate (fecha de grado) a Date usando
 * la zona horaria del sistema. Con los codecs nativos del driver la fecha se
 * guarda en UTC, asi un ISODate("2015-12-01T00:00:00Z") cargado por script se
 * lee como 2015-12-01 en cualquier maquina (en Bogota, UTC-5, sin este ajuste
 * se leeria como 2015-11-30).
 */
@Configuration
public class MongoConfig {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return MongoCustomConversions.create(adapter -> adapter.useNativeDriverJavaTimeCodecs());
	}
}

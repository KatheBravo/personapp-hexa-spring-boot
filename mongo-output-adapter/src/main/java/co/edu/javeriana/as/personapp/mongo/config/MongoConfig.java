package co.edu.javeriana.as.personapp.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * Configuración del adaptador de MongoDB.
 *
 * Spring Data convierte por defecto un {@code LocalDate} con la zona horaria del
 * sistema. Con los codecs nativos del driver la fecha se guarda y se lee en UTC,
 * de modo que un {@code ISODate("2015-12-01T00:00:00Z")} cargado por script se
 * lee como 2015-12-01 en cualquier máquina. Sin este ajuste, en Bogotá (UTC-5) se
 * leería como 2015-11-30.
 */
@Configuration
public class MongoConfig {

	/**
	 * Registra las conversiones de MongoDB con los codecs nativos del driver para los
	 * tipos de {@code java.time}.
	 *
	 * @return conversiones que usa Spring Data MongoDB
	 */
	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return MongoCustomConversions.create(adapter -> adapter.useNativeDriverJavaTimeCodecs());
	}
}

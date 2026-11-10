package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST para teléfonos.
 */
@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

	/** Adaptador de salida de teléfonos en MariaDB. */
	private final PhoneOutputPort phoneOutputPortMaria;

	/** Adaptador de salida de teléfonos en MongoDB. */
	private final PhoneOutputPort phoneOutputPortMongo;

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Conversor de los modelos REST de teléfono. */
	private final TelefonoMapperRest telefonoMapperRest;

	/**
	 * Recibe los adaptadores de salida de ambos motores y el conversor REST.
	 *
	 * @param phoneOutputPortMaria teléfonos en MariaDB
	 * @param phoneOutputPortMongo teléfonos en MongoDB
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param telefonoMapperRest conversor de teléfonos
	 */
	public TelefonoInputAdapterRest(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phoneOutputPortMaria,
			@Qualifier("phoneOutputAdapterMongo") PhoneOutputPort phoneOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			TelefonoMapperRest telefonoMapperRest) {
		this.phoneOutputPortMaria = phoneOutputPortMaria;
		this.phoneOutputPortMongo = phoneOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.telefonoMapperRest = telefonoMapperRest;
	}

	/**
	 * Crea un caso de uso conectado al motor indicado. Se crea uno por operación, como
	 * variable local y no como atributo del bean, para que dos peticiones con motores
	 * distintos no compartan estado. Los puertos de
	 * teléfonos y de personas siempre son del mismo motor, porque el dueño se valida
	 * en esa base de datos.
	 *
	 * @param dbOption motor de persistencia
	 * @return caso de uso de teléfonos del motor
	 */
	private PhoneInputPort setPhoneOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	/**
	 * Lista los teléfonos del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return teléfonos en formato de respuesta
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public List<TelefonoResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial TelefonoEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setPhoneOutputPortInjection(db).findAll().stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel())).toList();
	}

	/**
	 * Busca un teléfono por número.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param number número del teléfono
	 * @return el teléfono encontrado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si el teléfono no existe
	 */
	public TelefonoResponse buscarTelefono(String database, String number)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Phone phone = setPhoneOutputPortInjection(db).findOne(number);
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	/**
	 * Crea un teléfono en el motor indicado en la petición.
	 *
	 * @param request datos del teléfono y motor
	 * @return el teléfono creado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws AlreadyExistException si el número ya existe
	 * @throws NoExistException si el dueño no existe
	 */
	public TelefonoResponse crearTelefono(TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Phone phone = setPhoneOutputPortInjection(db).create(telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	/**
	 * Edita el operador o el dueño de un teléfono. El número de la ruta reemplaza el
	 * que venga en el cuerpo.
	 *
	 * @param number número del teléfono
	 * @param request datos nuevos y motor
	 * @return el teléfono actualizado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws NoExistException si el teléfono o el dueño no existen
	 */
	public TelefonoResponse editarTelefono(String number, TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setNumber(number);
		Phone phone = setPhoneOutputPortInjection(db).edit(number, telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	/**
	 * Elimina un teléfono.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param number número del teléfono
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si el teléfono no existe
	 */
	public Response eliminarTelefono(String database, String number) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setPhoneOutputPortInjection(db).drop(number);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Phone " + number + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel(),
				LocalDateTime.now());
	}

	/**
	 * Cuenta los teléfonos del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return número de teléfonos
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public Integer contarTelefonos(String database) throws InvalidOptionException {
		return setPhoneOutputPortInjection(DatabaseOption.from(database)).count();
	}
}

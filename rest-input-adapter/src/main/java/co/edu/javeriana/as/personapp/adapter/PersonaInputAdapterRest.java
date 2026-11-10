package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST para personas.
 *
 * Recibe por constructor las dos implementaciones del puerto de salida, MariaDB y
 * MongoDB, distinguidas con {@code @Qualifier}. En cada operación crea el caso de
 * uso con la implementación del motor solicitado; así el motor cambia en tiempo
 * de ejecución, sin recompilar ni reiniciar.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Conversor de los modelos REST de persona. */
	private final PersonaMapperRest personaMapperRest;

	/** Conversor de los modelos REST de teléfono. */
	private final TelefonoMapperRest telefonoMapperRest;

	/** Conversor de los modelos REST de estudio. */
	private final EstudioMapperRest estudioMapperRest;

	/**
	 * Recibe los adaptadores de salida de ambos motores y los conversores REST.
	 *
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param personaMapperRest conversor de personas
	 * @param telefonoMapperRest conversor de teléfonos
	 * @param estudioMapperRest conversor de estudios
	 */
	public PersonaInputAdapterRest(@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			PersonaMapperRest personaMapperRest, TelefonoMapperRest telefonoMapperRest,
			EstudioMapperRest estudioMapperRest) {
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.personaMapperRest = personaMapperRest;
		this.telefonoMapperRest = telefonoMapperRest;
		this.estudioMapperRest = estudioMapperRest;
	}

	/**
	 * Crea un caso de uso conectado al motor indicado. Se crea uno por operación, como
	 * variable local y no como atributo del bean, para que dos peticiones con motores
	 * distintos no compartan estado.
	 *
	 * @param dbOption motor de persistencia
	 * @return caso de uso de personas del motor
	 */
	private PersonInputPort setPersonOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new PersonUseCase(personOutputPortMaria)
				: new PersonUseCase(personOutputPortMongo);
	}

	/**
	 * Lista las personas del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return personas en formato de respuesta
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, db.getLabel())).toList();
	}

	/**
	 * Busca una persona por cédula.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return la persona encontrada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la persona no existe
	 */
	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Person person = setPersonOutputPortInjection(db).findOne(cc);
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	/**
	 * Crea una persona en el motor indicado en la petición.
	 *
	 * @param request datos de la persona y motor
	 * @return la persona creada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws AlreadyExistException si la cédula ya existe
	 */
	public PersonaResponse crearPersona(PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		log.info("Into crearPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Person person = setPersonOutputPortInjection(db).create(personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	/**
	 * Edita una persona. La cédula de la ruta reemplaza la que venga en el cuerpo.
	 *
	 * @param cc cédula de la persona
	 * @param request datos nuevos y motor
	 * @return la persona actualizada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws NoExistException si la persona no existe
	 */
	public PersonaResponse editarPersona(Integer cc, PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		log.info("Into editarPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setDni(String.valueOf(cc));
		Person person = setPersonOutputPortInjection(db).edit(cc, personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	/**
	 * Elimina una persona junto con sus teléfonos y estudios.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la persona no existe
	 */
	public Response eliminarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setPersonOutputPortInjection(db).drop(cc);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Person " + cc + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel() + " (with its phones and studies)",
				LocalDateTime.now());
	}

	/**
	 * Cuenta las personas del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return número de personas
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public Integer contarPersonas(String database) throws InvalidOptionException {
		return setPersonOutputPortInjection(DatabaseOption.from(database)).count();
	}

	/**
	 * Lista los teléfonos de una persona.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return teléfonos de la persona
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la persona no existe
	 */
	public List<TelefonoResponse> telefonosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).getPhones(cc).stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel())).toList();
	}

	/**
	 * Lista los estudios de una persona.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return estudios de la persona
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la persona no existe
	 */
	public List<EstudioResponse> estudiosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).getStudies(cc).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}

}

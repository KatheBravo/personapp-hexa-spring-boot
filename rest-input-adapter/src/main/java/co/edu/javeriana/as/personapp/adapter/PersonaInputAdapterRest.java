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
 * Aqui esta "la magia" del laboratorio: hay DOS implementaciones del mismo
 * puerto de salida (MariaDB y MongoDB). Con @Qualifier se reciben ambas por el
 * constructor y, en cada peticion, se arma el caso de uso con la que pidio el
 * cliente. El cambio de base de datos ocurre en tiempo de ejecucion (en
 * caliente), sin recompilar ni reiniciar.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final PersonaMapperRest personaMapperRest;
	private final TelefonoMapperRest telefonoMapperRest;
	private final EstudioMapperRest estudioMapperRest;

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
	 * Devuelve un caso de uso NUEVO conectado al motor pedido. Es una variable
	 * local (no un atributo del bean) para que dos peticiones simultaneas con
	 * motores distintos no se pisen.
	 */
	private PersonInputPort setPersonOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new PersonUseCase(personOutputPortMaria)
				: new PersonUseCase(personOutputPortMongo);
	}

	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, db.getLabel())).toList();
	}

	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Person person = setPersonOutputPortInjection(db).findOne(cc);
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	public PersonaResponse crearPersona(PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		log.info("Into crearPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Person person = setPersonOutputPortInjection(db).create(personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	public PersonaResponse editarPersona(Integer cc, PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		log.info("Into editarPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setDni(String.valueOf(cc));
		Person person = setPersonOutputPortInjection(db).edit(cc, personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, db.getLabel());
	}

	public Response eliminarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setPersonOutputPortInjection(db).drop(cc);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Person " + cc + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel() + " (with its phones and studies)",
				LocalDateTime.now());
	}

	public Integer contarPersonas(String database) throws InvalidOptionException {
		return setPersonOutputPortInjection(DatabaseOption.from(database)).count();
	}

	public List<TelefonoResponse> telefonosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).getPhones(cc).stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel())).toList();
	}

	public List<EstudioResponse> estudiosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setPersonOutputPortInjection(db).getStudies(cc).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}

}

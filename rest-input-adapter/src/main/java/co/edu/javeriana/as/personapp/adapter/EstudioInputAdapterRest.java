package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST para estudios.
 *
 * Recibe las dos implementaciones de cada puerto de salida y, en cada operación,
 * crea el caso de uso con las del motor solicitado.
 */
@Slf4j
@Adapter
public class EstudioInputAdapterRest {

	/** Adaptador de salida de estudios en MariaDB. */
	private final StudyOutputPort studyOutputPortMaria;

	/** Adaptador de salida de estudios en MongoDB. */
	private final StudyOutputPort studyOutputPortMongo;

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Adaptador de salida de profesiones en MariaDB. */
	private final ProfessionOutputPort professionOutputPortMaria;

	/** Adaptador de salida de profesiones en MongoDB. */
	private final ProfessionOutputPort professionOutputPortMongo;

	/** Conversor entre el dominio y los modelos REST de estudio. */
	private final EstudioMapperRest estudioMapperRest;

	/**
	 * Recibe los adaptadores de salida de ambos motores. Spring los distingue por el
	 * nombre del bean indicado en {@code @Qualifier}.
	 *
	 * @param studyOutputPortMaria estudios en MariaDB
	 * @param studyOutputPortMongo estudios en MongoDB
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param professionOutputPortMaria profesiones en MariaDB
	 * @param professionOutputPortMongo profesiones en MongoDB
	 * @param estudioMapperRest conversor de los modelos REST
	 */
	public EstudioInputAdapterRest(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyOutputPortMaria,
			@Qualifier("studyOutputAdapterMongo") StudyOutputPort studyOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			EstudioMapperRest estudioMapperRest) {
		this.studyOutputPortMaria = studyOutputPortMaria;
		this.studyOutputPortMongo = studyOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.estudioMapperRest = estudioMapperRest;
	}

	/**
	 * Crea un caso de uso conectado al motor indicado. Se crea uno por operación, como
	 * variable local y no como atributo del bean, para que dos peticiones con motores
	 * distintos no compartan estado. Los tres puertos
	 * (estudios, personas y profesiones) siempre son del mismo motor.
	 *
	 * @param dbOption motor de persistencia
	 * @return caso de uso de estudios del motor
	 */
	private StudyInputPort setStudyOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
	}

	/**
	 * Lista los estudios del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return estudios en formato de respuesta
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public List<EstudioResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial EstudiosEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setStudyOutputPortInjection(db).findAll().stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}

	/**
	 * Busca un estudio por su llave compuesta.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param personId cédula de la persona
	 * @param professionId identificador de la profesión
	 * @return el estudio encontrado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si el estudio no existe
	 */
	public EstudioResponse buscarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Study study = setStudyOutputPortInjection(db).findOne(personId, professionId);
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	/**
	 * Crea un estudio en el motor indicado en la petición.
	 *
	 * @param request datos del estudio y motor
	 * @return el estudio creado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws AlreadyExistException si el estudio ya existe
	 * @throws NoExistException si la persona o la profesión no existen
	 */
	public EstudioResponse crearEstudio(EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Study study = setStudyOutputPortInjection(db).create(estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	/**
	 * Edita la fecha de grado y la universidad de un estudio. La llave de la ruta
	 * reemplaza la que venga en el cuerpo.
	 *
	 * @param personId cédula de la persona
	 * @param professionId identificador de la profesión
	 * @param request datos nuevos y motor
	 * @return el estudio actualizado
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws NoExistException si el estudio no existe
	 */
	public EstudioResponse editarEstudio(Integer personId, Integer professionId, EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setPersonId(String.valueOf(personId));
		request.setProfessionId(String.valueOf(professionId));
		Study study = setStudyOutputPortInjection(db).edit(personId, professionId,
				estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	/**
	 * Elimina un estudio.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param personId cédula de la persona
	 * @param professionId identificador de la profesión
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si el estudio no existe
	 */
	public Response eliminarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setStudyOutputPortInjection(db).drop(personId, professionId);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Study of person " + personId + " in profession " + professionId
						+ (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in " + db.getLabel(),
				LocalDateTime.now());
	}

	/**
	 * Cuenta los estudios del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return número de estudios
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public Integer contarEstudios(String database) throws InvalidOptionException {
		return setStudyOutputPortInjection(DatabaseOption.from(database)).count();
	}
}

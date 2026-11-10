package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST para profesiones. En cada operación crea el caso de
 * uso con el adaptador de salida del motor solicitado.
 */
@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	/** Adaptador de salida de profesiones en MariaDB. */
	private final ProfessionOutputPort professionOutputPortMaria;

	/** Adaptador de salida de profesiones en MongoDB. */
	private final ProfessionOutputPort professionOutputPortMongo;

	/** Conversor de los modelos REST de profesión. */
	private final ProfesionMapperRest profesionMapperRest;

	/** Conversor de los modelos REST de estudio. */
	private final EstudioMapperRest estudioMapperRest;

	/**
	 * Recibe los adaptadores de salida de ambos motores y los conversores REST.
	 *
	 * @param professionOutputPortMaria profesiones en MariaDB
	 * @param professionOutputPortMongo profesiones en MongoDB
	 * @param profesionMapperRest conversor de profesiones
	 * @param estudioMapperRest conversor de estudios
	 */
	public ProfesionInputAdapterRest(
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			ProfesionMapperRest profesionMapperRest, EstudioMapperRest estudioMapperRest) {
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.profesionMapperRest = profesionMapperRest;
		this.estudioMapperRest = estudioMapperRest;
	}

	/**
	 * Crea un caso de uso conectado al motor indicado. Se crea uno por operación, como
	 * variable local y no como atributo del bean, para que dos peticiones con motores
	 * distintos no compartan estado.
	 *
	 * @param dbOption motor de persistencia
	 * @return caso de uso de profesiones del motor
	 */
	private ProfessionInputPort setProfessionOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new ProfessionUseCase(professionOutputPortMaria)
				: new ProfessionUseCase(professionOutputPortMongo);
	}

	/**
	 * Lista las profesiones del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return profesiones en formato de respuesta
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public List<ProfesionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial ProfesionEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setProfessionOutputPortInjection(db).findAll().stream()
				.map(profession -> profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel())).toList();
	}

	/**
	 * Busca una profesión por identificador.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return la profesión encontrada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la profesión no existe
	 */
	public ProfesionResponse buscarProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Profession profession = setProfessionOutputPortInjection(db).findOne(id);
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	/**
	 * Crea una profesión en el motor indicado en la petición.
	 *
	 * @param request datos de la profesión y motor
	 * @return la profesión creada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws AlreadyExistException si el identificador ya existe
	 */
	public ProfesionResponse crearProfesion(ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Profession profession = setProfessionOutputPortInjection(db)
				.create(profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	/**
	 * Edita una profesión. El identificador de la ruta reemplaza el que venga en el
	 * cuerpo.
	 *
	 * @param id identificador de la profesión
	 * @param request datos nuevos y motor
	 * @return la profesión actualizada
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws InvalidDataException si los datos no son válidos
	 * @throws NoExistException si la profesión no existe
	 */
	public ProfesionResponse editarProfesion(Integer id, ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setId(String.valueOf(id));
		Profession profession = setProfessionOutputPortInjection(db).edit(id,
				profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	/**
	 * Elimina una profesión junto con sus estudios.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la profesión no existe
	 */
	public Response eliminarProfesion(String database, Integer id) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setProfessionOutputPortInjection(db).drop(id);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Profession " + id + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel() + " (with its studies)",
				LocalDateTime.now());
	}

	/**
	 * Cuenta las profesiones del motor indicado.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @return número de profesiones
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public Integer contarProfesiones(String database) throws InvalidOptionException {
		return setProfessionOutputPortInjection(DatabaseOption.from(database)).count();
	}

	/**
	 * Lista los estudios registrados en una profesión.
	 *
	 * @param database motor solicitado: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return estudios de la profesión
	 * @throws InvalidOptionException si el motor no es válido
	 * @throws NoExistException si la profesión no existe
	 */
	public List<EstudioResponse> estudiosDeProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setProfessionOutputPortInjection(db).getStudies(id).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}
}

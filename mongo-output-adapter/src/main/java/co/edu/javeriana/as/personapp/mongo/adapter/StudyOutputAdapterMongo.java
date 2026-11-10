package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de estudios para MongoDB. Nombre del bean:
 * {@code studyOutputAdapterMongo}.
 *
 * MongoDB no tiene llaves compuestas: el {@code _id} de cada estudio se arma con
 * {@link EstudiosMapperMongo#buildId(Integer, Integer)}.
 */
@Slf4j
@Adapter("studyOutputAdapterMongo")
@RequiredArgsConstructor
public class StudyOutputAdapterMongo implements StudyOutputPort {

	/** Repositorio de la colección estudios. */
	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	/** Conversor entre {@link Study} y {@code EstudiosDocument}. */
	private final EstudiosMapperMongo estudiosMapperMongo;

	/**
	 * Guarda el estudio y lo vuelve a leer para resolver las referencias a la persona
	 * y la profesión.
	 *
	 * @param study estudio a guardar
	 * @return el estudio guardado con los datos de la persona y la profesión
	 */
	@Override
	public Study save(Study study) {
		log.debug("Into save study on Adapter MongoDB");
		estudiosRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
		return findById(study.getPerson().getIdentification(), study.getProfession().getIdentification());
	}

	/**
	 * Elimina el estudio.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return {@code true} si el estudio ya no existe
	 */
	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete study on Adapter MongoDB");
		String id = EstudiosMapperMongo.buildId(personIdentification, professionIdentification);
		estudiosRepositoryMongo.deleteById(id);
		return !estudiosRepositoryMongo.existsById(id);
	}

	/**
	 * Consulta todos los estudios de MongoDB.
	 *
	 * @return lista de estudios con los datos de la persona y la profesión
	 */
	@Override
	public List<Study> find() {
		log.debug("Into find study on Adapter MongoDB");
		return estudiosRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca un estudio por su llave compuesta en MongoDB.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return el estudio encontrado, o {@code null} si no existe
	 */
	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById study on Adapter MongoDB");
		String id = EstudiosMapperMongo.buildId(personIdentification, professionIdentification);
		return estudiosRepositoryMongo.findById(id).map(estudiosMapperMongo::fromAdapterToDomain).orElse(null);
	}
}

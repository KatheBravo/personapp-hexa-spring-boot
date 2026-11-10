package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de profesiones para MongoDB. Nombre del bean:
 * {@code professionOutputAdapterMongo}.
 */
@Slf4j
@Adapter("professionOutputAdapterMongo")
@RequiredArgsConstructor
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {

	/** Repositorio de la colección profesion. */
	private final ProfesionRepositoryMongo profesionRepositoryMongo;

	/** Repositorio de la colección estudios, usado para el borrado en cascada. */
	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	/** Conversor entre {@link Profession} y {@code ProfesionDocument}. */
	private final ProfesionMapperMongo profesionMapperMongo;

	/**
	 * Guarda la profesión y la vuelve a leer para devolverla con sus estudios.
	 *
	 * @param profession profesión a guardar
	 * @return la profesión guardada
	 */
	@Override
	public Profession save(Profession profession) {
		log.debug("Into save profession on Adapter MongoDB");
		profesionRepositoryMongo.save(profesionMapperMongo.fromDomainToAdapter(profession));
		return findById(profession.getIdentification());
	}

	/**
	 * Elimina la profesión y, antes, los estudios que la referencian.
	 *
	 * @param identification identificador de la profesión
	 * @return {@code true} si la profesión ya no existe
	 */
	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete profession on Adapter MongoDB");
		estudiosRepositoryMongo.eliminarPorProfesion(identification);
		profesionRepositoryMongo.deleteById(identification);
		return !profesionRepositoryMongo.existsById(identification);
	}

	/**
	 * Consulta todas las profesiones de MongoDB.
	 *
	 * @return lista de profesiones con sus estudios
	 */
	@Override
	public List<Profession> find() {
		log.debug("Into find profession on Adapter MongoDB");
		return profesionRepositoryMongo.findAll().stream().map(profesionMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca una profesión por identificador en MongoDB.
	 *
	 * @param identification identificador de la profesión
	 * @return la profesión encontrada, o {@code null} si no existe
	 */
	@Override
	public Profession findById(Integer identification) {
		log.debug("Into findById profession on Adapter MongoDB");
		return profesionRepositoryMongo.findById(identification).map(profesionMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}
}

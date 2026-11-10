package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.ProfesionMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de profesiones para MariaDB. Nombre del bean:
 * {@code professionOutputAdapterMaria}.
 */
@Slf4j
@Adapter("professionOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class ProfessionOutputAdapterMaria implements ProfessionOutputPort {

	/** Repositorio JPA de la tabla profesion. */
	private final ProfesionRepositoryMaria profesionRepositoryMaria;

	/** Conversor entre {@link Profession} y {@link ProfesionEntity}. */
	private final ProfesionMapperMaria profesionMapperMaria;

	/**
	 * Guarda la profesión. Si ya existe, copia sus datos sobre la entidad
	 * administrada para no perder sus estudios.
	 *
	 * @param profession profesión a guardar
	 * @return la profesión guardada
	 */
	@Override
	public Profession save(Profession profession) {
		log.debug("Into save profession on Adapter MariaDB");
		ProfesionEntity profesionEntity = profesionRepositoryMaria.findById(profession.getIdentification())
				.map(existing -> profesionMapperMaria.copyToAdapter(profession, existing))
				.orElseGet(() -> profesionMapperMaria.fromDomainToAdapter(profession));
		return profesionMapperMaria.fromAdapterToDomain(profesionRepositoryMaria.save(profesionEntity));
	}

	/**
	 * Elimina la profesión. El {@code cascade} de {@link ProfesionEntity} elimina
	 * también sus estudios.
	 *
	 * @param identification identificador de la profesión
	 * @return {@code true} si la profesión ya no existe
	 */
	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete profession on Adapter MariaDB");
		profesionRepositoryMaria.deleteById(identification);
		profesionRepositoryMaria.flush();
		return profesionRepositoryMaria.findById(identification).isEmpty();
	}

	/**
	 * Consulta todas las profesiones de MariaDB.
	 *
	 * @return lista de profesiones con sus estudios
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Profession> find() {
		log.debug("Into find profession on Adapter MariaDB");
		return profesionRepositoryMaria.findAll().stream().map(profesionMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca una profesión por identificador en MariaDB.
	 *
	 * @param identification identificador de la profesión
	 * @return la profesión encontrada, o {@code null} si no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public Profession findById(Integer identification) {
		log.debug("Into findById profession on Adapter MariaDB");
		return profesionRepositoryMaria.findById(identification).map(profesionMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}
}

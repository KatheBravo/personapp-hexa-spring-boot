package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de estudios para MariaDB. Nombre del bean:
 * {@code studyOutputAdapterMaria}.
 *
 * La tabla {@code estudios} usa la llave compuesta {@link EstudiosEntityPK}
 * (id_prof, cc_per).
 */
@Slf4j
@Adapter("studyOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class StudyOutputAdapterMaria implements StudyOutputPort {

	/** Repositorio JPA de la tabla estudios. */
	private final EstudiosRepositoryMaria estudiosRepositoryMaria;

	/** Repositorio JPA de la tabla persona. */
	private final PersonaRepositoryMaria personaRepositoryMaria;

	/** Repositorio JPA de la tabla profesion. */
	private final ProfesionRepositoryMaria profesionRepositoryMaria;

	/** Conversor entre {@link Study} y {@link EstudiosEntity}. */
	private final EstudiosMapperMaria estudiosMapperMaria;

	/**
	 * Guarda el estudio. La llave compuesta define las columnas cc_per e id_prof; las
	 * relaciones se llenan con las entidades administradas para devolver el estudio
	 * completo.
	 *
	 * @param study estudio a guardar; la persona y la profesión deben existir
	 * @return el estudio guardado con los datos de la persona y la profesión
	 * @throws java.util.NoSuchElementException si la persona o la profesión no existen en MariaDB
	 */
	@Override
	public Study save(Study study) {
		log.debug("Into save study on Adapter MariaDB");
		EstudiosEntity estudiosEntity = estudiosMapperMaria.fromDomainToAdapter(study);
		estudiosEntity.setPersona(personaRepositoryMaria.findById(study.getPerson().getIdentification()).orElseThrow());
		estudiosEntity.setProfesion(
				profesionRepositoryMaria.findById(study.getProfession().getIdentification()).orElseThrow());
		return estudiosMapperMaria.fromAdapterToDomain(estudiosRepositoryMaria.save(estudiosEntity));
	}

	/**
	 * Elimina el estudio. Antes lo retira de las listas de la persona y de la
	 * profesión para que su {@code cascade} no lo vuelva a guardar.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return {@code true} si el estudio ya no existe
	 */
	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete study on Adapter MariaDB");
		EstudiosEntityPK pk = new EstudiosEntityPK(professionIdentification, personIdentification);
		estudiosRepositoryMaria.findById(pk).ifPresent(estudio -> {
			if (estudio.getPersona() != null && estudio.getPersona().getEstudios() != null) {
				estudio.getPersona().getEstudios().remove(estudio);
			}
			if (estudio.getProfesion() != null && estudio.getProfesion().getEstudios() != null) {
				estudio.getProfesion().getEstudios().remove(estudio);
			}
			estudiosRepositoryMaria.delete(estudio);
		});
		estudiosRepositoryMaria.flush();
		return estudiosRepositoryMaria.findById(pk).isEmpty();
	}

	/**
	 * Consulta todos los estudios de MariaDB.
	 *
	 * @return lista de estudios con los datos de la persona y la profesión
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Study> find() {
		log.debug("Into find study on Adapter MariaDB");
		return estudiosRepositoryMaria.findAll().stream().map(estudiosMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca un estudio por su llave compuesta en MariaDB.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return el estudio encontrado, o {@code null} si no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById study on Adapter MariaDB");
		// La llave se construye en el orden de la tabla: (id_prof, cc_per)
		EstudiosEntityPK pk = new EstudiosEntityPK(professionIdentification, personIdentification);
		return estudiosRepositoryMaria.findById(pk).map(estudiosMapperMaria::fromAdapterToDomain).orElse(null);
	}
}

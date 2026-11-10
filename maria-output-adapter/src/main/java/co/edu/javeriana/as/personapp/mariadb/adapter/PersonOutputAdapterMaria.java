package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.PersonaMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de personas para MariaDB.
 *
 * Implementa {@link PersonOutputPort} con Spring Data JPA. Los adaptadores de
 * entrada eligen este motor con {@code @Qualifier("personOutputAdapterMaria")}.
 */
@Slf4j
@Adapter("personOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class PersonOutputAdapterMaria implements PersonOutputPort {

	/** Repositorio JPA de la tabla persona. */
	private final PersonaRepositoryMaria personaRepositoryMaria;

	/** Conversor entre {@link Person} y {@link PersonaEntity}. */
	private final PersonaMapperMaria personaMapperMaria;

	/**
	 * Guarda la persona. Si ya existe, copia sus datos sobre la entidad administrada
	 * para no perder sus teléfonos y estudios.
	 *
	 * @param person persona a guardar
	 * @return la persona guardada, con sus relaciones
	 */
	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MariaDB");
		PersonaEntity personaEntity = personaRepositoryMaria.findById(person.getIdentification())
				.map(existing -> personaMapperMaria.copyToAdapter(person, existing))
				.orElseGet(() -> personaMapperMaria.fromDomainToAdapter(person));
		PersonaEntity persistedPersona = personaRepositoryMaria.save(personaEntity);
		return personaMapperMaria.fromAdapterToDomain(persistedPersona);
	}

	/**
	 * Elimina la persona. El {@code cascade} de {@link PersonaEntity} elimina también
	 * sus teléfonos y estudios.
	 *
	 * @param identification cédula de la persona
	 * @return {@code true} si la persona ya no existe
	 */
	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MariaDB");
		personaRepositoryMaria.deleteById(identification);
		personaRepositoryMaria.flush();
		return personaRepositoryMaria.findById(identification).isEmpty();
	}

	/**
	 * Consulta todas las personas de MariaDB.
	 *
	 * @return lista de personas con sus teléfonos y estudios
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Person> find() {
		log.debug("Into find on Adapter MariaDB");
		return personaRepositoryMaria.findAll().stream().map(personaMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca una persona por cédula en MariaDB.
	 *
	 * @param identification cédula de la persona
	 * @return la persona encontrada, o {@code null} si no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MariaDB");
		return personaRepositoryMaria.findById(identification).map(personaMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}

}

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
 * Adaptador de salida (MariaDB) del puerto PersonOutputPort. El nombre del
 * bean ("personOutputAdapterMaria") es el que usa @Qualifier para elegir esta
 * implementacion en tiempo de ejecucion.
 */
@Slf4j
@Adapter("personOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class PersonOutputAdapterMaria implements PersonOutputPort {

	private final PersonaRepositoryMaria personaRepositoryMaria;

	private final PersonaMapperMaria personaMapperMaria;

	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MariaDB");
		// Si ya existe se actualizan solo sus datos, sin tocar telefonos ni estudios
		PersonaEntity personaEntity = personaRepositoryMaria.findById(person.getIdentification())
				.map(existing -> personaMapperMaria.copyToAdapter(person, existing))
				.orElseGet(() -> personaMapperMaria.fromDomainToAdapter(person));
		PersonaEntity persistedPersona = personaRepositoryMaria.save(personaEntity);
		return personaMapperMaria.fromAdapterToDomain(persistedPersona);
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MariaDB");
		// cascade = ALL en PersonaEntity borra tambien sus telefonos y estudios
		personaRepositoryMaria.deleteById(identification);
		personaRepositoryMaria.flush();
		return personaRepositoryMaria.findById(identification).isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Person> find() {
		log.debug("Into find on Adapter MariaDB");
		return personaRepositoryMaria.findAll().stream().map(personaMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MariaDB");
		return personaRepositoryMaria.findById(identification).map(personaMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}

}

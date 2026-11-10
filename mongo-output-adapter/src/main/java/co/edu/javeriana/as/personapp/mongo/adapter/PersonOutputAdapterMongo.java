package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida (MongoDB) del puerto PersonOutputPort. Implementa la
 * MISMA interfaz que PersonOutputAdapterMaria; el adaptador de entrada decide
 * cual usar con el nombre del bean.
 */
@Slf4j
@Adapter("personOutputAdapterMongo")
@RequiredArgsConstructor
public class PersonOutputAdapterMongo implements PersonOutputPort {

	private final PersonaRepositoryMongo personaRepositoryMongo;

	private final TelefonoRepositoryMongo telefonoRepositoryMongo;

	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	private final PersonaMapperMongo personaMapperMongo;

	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MongoDB");
		personaRepositoryMongo.save(personaMapperMongo.fromDomainToAdapter(person));
		// Se vuelve a leer para traer telefonos y estudios (referencias inversas)
		return findById(person.getIdentification());
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		// MongoDB no tiene llaves foraneas: se borran a mano los documentos que referencian a la persona
		telefonoRepositoryMongo.eliminarPorDuenio(identification);
		estudiosRepositoryMongo.eliminarPorPersona(identification);
		personaRepositoryMongo.deleteById(identification);
		return !personaRepositoryMongo.existsById(identification);
	}

	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MongoDB");
		return personaRepositoryMongo.findAll().stream().map(personaMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return personaRepositoryMongo.findById(identification).map(personaMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}

}

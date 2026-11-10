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
 * Adaptador de salida de personas para MongoDB.
 *
 * Implementa el mismo puerto que el adaptador de MariaDB. Los adaptadores de
 * entrada lo eligen con {@code @Qualifier("personOutputAdapterMongo")}.
 */
@Slf4j
@Adapter("personOutputAdapterMongo")
@RequiredArgsConstructor
public class PersonOutputAdapterMongo implements PersonOutputPort {

	/** Repositorio de la colección persona. */
	private final PersonaRepositoryMongo personaRepositoryMongo;

	/** Repositorio de la colección telefono, usado para el borrado en cascada. */
	private final TelefonoRepositoryMongo telefonoRepositoryMongo;

	/** Repositorio de la colección estudios, usado para el borrado en cascada. */
	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	/** Conversor entre {@link Person} y {@code PersonaDocument}. */
	private final PersonaMapperMongo personaMapperMongo;

	/**
	 * Guarda la persona y la vuelve a leer para devolverla con sus teléfonos y
	 * estudios, que se consultan por referencia inversa.
	 *
	 * @param person persona a guardar
	 * @return la persona guardada, con sus relaciones
	 */
	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MongoDB");
		personaRepositoryMongo.save(personaMapperMongo.fromDomainToAdapter(person));
		return findById(person.getIdentification());
	}

	/**
	 * Elimina la persona. MongoDB no tiene llaves foráneas, así que primero se
	 * borran los teléfonos y estudios que la referencian.
	 *
	 * @param identification cédula de la persona
	 * @return {@code true} si la persona ya no existe
	 */
	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		telefonoRepositoryMongo.eliminarPorDuenio(identification);
		estudiosRepositoryMongo.eliminarPorPersona(identification);
		personaRepositoryMongo.deleteById(identification);
		return !personaRepositoryMongo.existsById(identification);
	}

	/**
	 * Consulta todas las personas de MongoDB.
	 *
	 * @return lista de personas con sus teléfonos y estudios
	 */
	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MongoDB");
		return personaRepositoryMongo.findAll().stream().map(personaMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca una persona por cédula en MongoDB.
	 *
	 * @param identification cédula de la persona
	 * @return la persona encontrada, o {@code null} si no existe
	 */
	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return personaRepositoryMongo.findById(identification).map(personaMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}

}

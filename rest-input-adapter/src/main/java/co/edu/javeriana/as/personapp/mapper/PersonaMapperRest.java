package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

/**
 * Convierte personas entre el dominio y los modelos de la API REST.
 */
@Mapper
public class PersonaMapperRest {

	/**
	 * Convierte una persona en respuesta REST indicando el motor MariaDB.
	 *
	 * @param person persona del dominio
	 * @return respuesta REST
	 */
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}

	/**
	 * Convierte una persona en respuesta REST indicando el motor MongoDB.
	 *
	 * @param person persona del dominio
	 * @return respuesta REST
	 */
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}

	/**
	 * Convierte una persona del dominio en respuesta REST.
	 *
	 * @param person persona del dominio
	 * @param database nombre del motor que atendió la operación
	 * @return respuesta con los datos de la persona
	 */
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				RestValues.text(person.getIdentification()),
				person.getFirstName(),
				person.getLastName(),
				RestValues.text(person.getAge()),
				RestValues.sex(person.getGender()),
				database,
				"OK");
	}

	/**
	 * Convierte la petición REST en persona del dominio.
	 *
	 * @param request datos recibidos en el cuerpo
	 * @return persona del dominio
	 * @throws InvalidDataException si falta un campo obligatorio, la cédula o la edad no son
	 *         números, o el género no es M ni F
	 */
	public Person fromAdapterToDomain(PersonaRequest request) throws InvalidDataException {
		Person person = new Person();
		person.setIdentification(RestValues.requiredInteger(request.getDni(), "dni"));
		person.setFirstName(RestValues.requiredText(request.getFirstName(), "firstName"));
		person.setLastName(RestValues.requiredText(request.getLastName(), "lastName"));
		person.setGender(RestValues.gender(request.getSex()));
		person.setAge(RestValues.optionalInteger(request.getAge(), "age"));
		return person;
	}

}

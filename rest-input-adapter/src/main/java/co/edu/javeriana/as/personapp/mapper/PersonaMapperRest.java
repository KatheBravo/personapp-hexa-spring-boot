package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {

	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}

	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}

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
	 * ERROR DE LA PLANTILLA: este metodo devolvia "new Person()" vacio, por eso
	 * crear una persona por REST nunca funcionaba.
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

package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

/**
 * Convierte personas entre el dominio y el modelo de consola.
 */
@Mapper
public class PersonaMapperCli {

	/**
	 * Convierte una persona del dominio en el modelo de consola.
	 *
	 * @param person persona del dominio
	 * @return modelo con el género como M, F o -
	 */
	public PersonaModelCli fromDomainToAdapterCli(Person person) {
		PersonaModelCli personaModelCli = new PersonaModelCli();
		personaModelCli.setCc(person.getIdentification());
		personaModelCli.setNombre(person.getFirstName());
		personaModelCli.setApellido(person.getLastName());
		personaModelCli.setGenero(person.getGender() == Gender.MALE ? "M" : person.getGender() == Gender.FEMALE ? "F" : "-");
		personaModelCli.setEdad(person.getAge());
		return personaModelCli;
	}

	/**
	 * Convierte los datos leídos de la consola en persona del dominio.
	 *
	 * @param personaModelCli datos leídos; el género ya viene validado como M o F
	 * @return persona del dominio
	 */
	public Person fromAdapterCliToDomain(PersonaModelCli personaModelCli) {
		Person person = new Person();
		person.setIdentification(personaModelCli.getCc());
		person.setFirstName(personaModelCli.getNombre());
		person.setLastName(personaModelCli.getApellido());
		person.setGender("F".equalsIgnoreCase(personaModelCli.getGenero()) ? Gender.FEMALE : Gender.MALE);
		person.setAge(personaModelCli.getEdad());
		return person;
	}
}

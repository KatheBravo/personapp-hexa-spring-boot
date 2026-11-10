package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PersonUseCase implements PersonInputPort {

	private PersonOutputPort personPersintence;

	public PersonUseCase(@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	@Override
	public void setPersintence(PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	@Override
	public Person create(Person person) throws AlreadyExistException, InvalidDataException {
		log.debug("Into create on Application Domain");
		validate(person);
		if (personPersintence.findById(person.getIdentification()) != null) {
			throw new AlreadyExistException(
					"The person with id " + person.getIdentification() + " already exists into db, cannot be created");
		}
		return personPersintence.save(person);
	}

	@Override
	public Person edit(Integer identification, Person person) throws NoExistException, InvalidDataException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson == null) {
			throw new NoExistException(
					"The person with id " + identification + " does not exist into db, cannot be edited");
		}
		// La cedula es la llave: no se cambia al editar, manda la que viene en la ruta
		person.setIdentification(identification);
		validate(person);
		return personPersintence.save(person);
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return personPersintence.delete(identification);
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Person> findAll() {
		log.info("Output: " + personPersintence.getClass());
		return personPersintence.find();
	}

	@Override
	public Person findOne(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson;
		throw new NoExistException("The person with id " + identification + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Phone> getPhones(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson.getPhoneNumbers();
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be getting phones");
	}

	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson.getStudies();
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be getting studies");
	}

	/** Reglas de negocio de una persona antes de guardarla. */
	private void validate(Person person) throws InvalidDataException {
		if (person == null) {
			throw new InvalidDataException("The person is required");
		}
		if (!person.isValidIdentification()) {
			throw new InvalidDataException("The identification must be a positive number");
		}
		if (!person.isValidName()) {
			throw new InvalidDataException("First name and last name are required (max 45 characters)");
		}
		if (person.getGender() == null) {
			throw new InvalidDataException("The gender is required (M or F)");
		}
		if (!person.isValidAge()) {
			throw new InvalidDataException("The age cannot be negative");
		}
	}
}

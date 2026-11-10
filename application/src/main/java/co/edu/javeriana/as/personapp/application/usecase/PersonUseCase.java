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

/**
 * Caso de uso de personas: aplica las reglas de negocio y delega la persistencia
 * en el puerto de salida.
 *
 * El adaptador de salida llega por constructor; por defecto Spring inyecta el de
 * MariaDB. Los adaptadores de entrada crean una instancia con el motor que pide
 * cada operación.
 */
@Slf4j
@UseCase
public class PersonUseCase implements PersonInputPort {

	/** Adaptador de salida del motor de persistencia en uso. */
	private PersonOutputPort personPersintence;

	/**
	 * Crea el caso de uso con el adaptador de salida indicado.
	 *
	 * @param personPersintence adaptador de salida de personas
	 */
	public PersonUseCase(@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	/**
	 * Cambia el adaptador de salida de personas.
	 *
	 * @param personPersintence adaptador de salida del motor elegido
	 */
	@Override
	public void setPersintence(PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	/**
	 * Valida los datos y registra la persona si su cédula no existe.
	 *
	 * @param person datos de la persona
	 * @return la persona guardada
	 * @throws AlreadyExistException si la cédula ya está registrada
	 * @throws InvalidDataException si los datos no son válidos
	 */
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

	/**
	 * Actualiza una persona existente conservando la cédula recibida.
	 *
	 * @param identification cédula de la persona
	 * @param person datos nuevos
	 * @return la persona actualizada
	 * @throws NoExistException si la persona no existe
	 * @throws InvalidDataException si los datos nuevos no son válidos
	 */
	@Override
	public Person edit(Integer identification, Person person) throws NoExistException, InvalidDataException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson == null) {
			throw new NoExistException(
					"The person with id " + identification + " does not exist into db, cannot be edited");
		}
		// La cédula identifica a la persona: se conserva la recibida como parámetro
		person.setIdentification(identification);
		validate(person);
		return personPersintence.save(person);
	}

	/**
	 * Elimina la persona si existe.
	 *
	 * @param identification cédula de la persona
	 * @return {@code true} si quedó eliminada
	 * @throws NoExistException si la persona no existe
	 */
	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return personPersintence.delete(identification);
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be dropped");
	}

	/**
	 * Consulta todas las personas del motor configurado.
	 *
	 * @return lista de personas
	 */
	@Override
	public List<Person> findAll() {
		log.info("Output: " + personPersintence.getClass());
		return personPersintence.find();
	}

	/**
	 * Busca una persona por cédula.
	 *
	 * @param identification cédula de la persona
	 * @return la persona encontrada
	 * @throws NoExistException si la persona no existe
	 */
	@Override
	public Person findOne(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson;
		throw new NoExistException("The person with id " + identification + " does not exist into db, cannot be found");
	}

	/**
	 * Cuenta las personas del motor configurado.
	 *
	 * @return número de personas
	 */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/**
	 * Consulta los teléfonos de una persona.
	 *
	 * @param identification cédula de la persona
	 * @return teléfonos de la persona
	 * @throws NoExistException si la persona no existe
	 */
	@Override
	public List<Phone> getPhones(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson.getPhoneNumbers();
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be getting phones");
	}

	/**
	 * Consulta los estudios de una persona.
	 *
	 * @param identification cédula de la persona
	 * @return estudios de la persona
	 * @throws NoExistException si la persona no existe
	 */
	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		Person oldPerson = personPersintence.findById(identification);
		if (oldPerson != null)
			return oldPerson.getStudies();
		throw new NoExistException(
				"The person with id " + identification + " does not exist into db, cannot be getting studies");
	}

	/**
	 * Aplica las reglas de negocio de una persona antes de guardarla: cédula
	 * positiva, nombre y apellido obligatorios, género obligatorio y edad no negativa.
	 *
	 * @param person persona a validar
	 * @throws InvalidDataException con el motivo de la primera regla que no se cumple
	 */
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

package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de uso de teléfonos: valida el número y el operador, y que el dueño exista
 * en el mismo motor de persistencia.
 */
@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	/** Adaptador de salida de teléfonos. */
	private PhoneOutputPort phonePersistence;

	/** Adaptador de salida de personas, usado para validar el dueño. */
	private PersonOutputPort personPersistence;

	/**
	 * Crea el caso de uso con los adaptadores de salida indicados. Por defecto Spring
	 * inyecta los de MariaDB.
	 *
	 * @param phonePersistence adaptador de salida de teléfonos
	 * @param personPersistence adaptador de salida de personas del mismo motor
	 */
	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersistence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence) {
		this.phonePersistence = phonePersistence;
		this.personPersistence = personPersistence;
	}

	/**
	 * Cambia los adaptadores de salida.
	 *
	 * @param phonePersistence adaptador de salida de teléfonos
	 * @param personPersistence adaptador de salida de personas del mismo motor
	 */
	@Override
	public void setPersistence(PhoneOutputPort phonePersistence, PersonOutputPort personPersistence) {
		this.phonePersistence = phonePersistence;
		this.personPersistence = personPersistence;
	}

	/**
	 * Valida el teléfono, carga los datos del dueño y lo registra si el número no
	 * existe.
	 *
	 * @param phone datos del teléfono
	 * @return el teléfono guardado con los datos del dueño
	 * @throws AlreadyExistException si el número ya está registrado
	 * @throws NoExistException si el dueño no existe
	 * @throws InvalidDataException si los datos no son válidos
	 */
	@Override
	public Phone create(Phone phone) throws AlreadyExistException, NoExistException, InvalidDataException {
		log.debug("Into create phone on Application Domain");
		validate(phone);
		phone.setOwner(findOwner(phone));
		if (phonePersistence.findById(phone.getNumber()) != null) {
			throw new AlreadyExistException(
					"The phone " + phone.getNumber() + " already exists into db, cannot be created");
		}
		return phonePersistence.save(phone);
	}

	/**
	 * Actualiza el operador o el dueño de un teléfono existente. Se conserva el
	 * número recibido.
	 *
	 * @param number número del teléfono
	 * @param phone datos nuevos
	 * @return el teléfono actualizado
	 * @throws NoExistException si el teléfono o el dueño no existen
	 * @throws InvalidDataException si los datos nuevos no son válidos
	 */
	@Override
	public Phone edit(String number, Phone phone) throws NoExistException, InvalidDataException {
		if (phonePersistence.findById(number) == null) {
			throw new NoExistException("The phone " + number + " does not exist into db, cannot be edited");
		}
		phone.setNumber(number);
		validate(phone);
		phone.setOwner(findOwner(phone));
		return phonePersistence.save(phone);
	}

	/**
	 * Elimina el teléfono si existe.
	 *
	 * @param number número del teléfono
	 * @return {@code true} si quedó eliminado
	 * @throws NoExistException si el teléfono no existe
	 */
	@Override
	public Boolean drop(String number) throws NoExistException {
		if (phonePersistence.findById(number) == null) {
			throw new NoExistException("The phone " + number + " does not exist into db, cannot be dropped");
		}
		return phonePersistence.delete(number);
	}

	/**
	 * Consulta todos los teléfonos del motor configurado.
	 *
	 * @return lista de teléfonos
	 */
	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersistence.getClass());
		return phonePersistence.find();
	}

	/**
	 * Busca un teléfono por número.
	 *
	 * @param number número del teléfono
	 * @return el teléfono encontrado
	 * @throws NoExistException si el teléfono no existe
	 */
	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone phone = phonePersistence.findById(number);
		if (phone == null) {
			throw new NoExistException("The phone " + number + " does not exist into db, cannot be found");
		}
		return phone;
	}

	/**
	 * Cuenta los teléfonos del motor configurado.
	 *
	 * @return número de teléfonos
	 */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/**
	 * Busca en el motor configurado a la persona dueña del teléfono.
	 *
	 * @param phone teléfono con la cédula del dueño
	 * @return la persona dueña con todos sus datos
	 * @throws NoExistException si no existe una persona con esa cédula
	 */
	private Person findOwner(Phone phone) throws NoExistException {
		Integer ownerId = phone.getOwner().getIdentification();
		Person owner = personPersistence.findById(ownerId);
		if (owner == null) {
			throw new NoExistException("The owner (person) with id " + ownerId + " does not exist into db");
		}
		return owner;
	}

	/**
	 * Aplica las reglas de negocio de un teléfono: número de 7 a 15 dígitos,
	 * operador obligatorio de máximo 45 caracteres y cédula del dueño informada.
	 *
	 * @param phone teléfono a validar
	 * @throws InvalidDataException con el motivo de la primera regla que no se cumple
	 */
	private void validate(Phone phone) throws InvalidDataException {
		if (phone == null) {
			throw new InvalidDataException("The phone is required");
		}
		if (!phone.isValidNumber()) {
			throw new InvalidDataException("The phone number must have only digits (7 to 15)");
		}
		if (!phone.isValidCompany()) {
			throw new InvalidDataException("The phone company (operator) is required (max 45 characters)");
		}
		if (phone.getOwner() == null || phone.getOwner().getIdentification() == null) {
			throw new InvalidDataException("The phone owner (person id) is required");
		}
	}
}

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

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	private PhoneOutputPort phonePersistence;
	private PersonOutputPort personPersistence;

	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersistence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence) {
		this.phonePersistence = phonePersistence;
		this.personPersistence = personPersistence;
	}

	@Override
	public void setPersistence(PhoneOutputPort phonePersistence, PersonOutputPort personPersistence) {
		this.phonePersistence = phonePersistence;
		this.personPersistence = personPersistence;
	}

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

	@Override
	public Boolean drop(String number) throws NoExistException {
		if (phonePersistence.findById(number) == null) {
			throw new NoExistException("The phone " + number + " does not exist into db, cannot be dropped");
		}
		return phonePersistence.delete(number);
	}

	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersistence.getClass());
		return phonePersistence.find();
	}

	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone phone = phonePersistence.findById(number);
		if (phone == null) {
			throw new NoExistException("The phone " + number + " does not exist into db, cannot be found");
		}
		return phone;
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	/** Regla de negocio: un telefono siempre pertenece a una persona que existe. */
	private Person findOwner(Phone phone) throws NoExistException {
		Integer ownerId = phone.getOwner().getIdentification();
		Person owner = personPersistence.findById(ownerId);
		if (owner == null) {
			throw new NoExistException("The owner (person) with id " + ownerId + " does not exist into db");
		}
		return owner;
	}

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

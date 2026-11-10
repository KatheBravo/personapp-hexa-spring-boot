package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

	private ProfessionOutputPort professionPersistence;

	public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersistence) {
		this.professionPersistence = professionPersistence;
	}

	@Override
	public void setPersistence(ProfessionOutputPort professionPersistence) {
		this.professionPersistence = professionPersistence;
	}

	@Override
	public Profession create(Profession profession) throws AlreadyExistException, InvalidDataException {
		log.debug("Into create profession on Application Domain");
		validate(profession);
		if (professionPersistence.findById(profession.getIdentification()) != null) {
			throw new AlreadyExistException("The profession with id " + profession.getIdentification()
					+ " already exists into db, cannot be created");
		}
		return professionPersistence.save(profession);
	}

	@Override
	public Profession edit(Integer identification, Profession profession)
			throws NoExistException, InvalidDataException {
		if (professionPersistence.findById(identification) == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be edited");
		}
		profession.setIdentification(identification);
		validate(profession);
		return professionPersistence.save(profession);
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		if (professionPersistence.findById(identification) == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be dropped");
		}
		return professionPersistence.delete(identification);
	}

	@Override
	public List<Profession> findAll() {
		log.info("Output: " + professionPersistence.getClass());
		return professionPersistence.find();
	}

	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession profession = professionPersistence.findById(identification);
		if (profession == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be found");
		}
		return profession;
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		return findOne(identification).getStudies();
	}

	private void validate(Profession profession) throws InvalidDataException {
		if (profession == null) {
			throw new InvalidDataException("The profession is required");
		}
		if (!profession.isValidIdentification()) {
			throw new InvalidDataException("The profession id must be a positive number");
		}
		if (!profession.isValidName()) {
			throw new InvalidDataException("The profession name is required (max 90 characters)");
		}
	}
}

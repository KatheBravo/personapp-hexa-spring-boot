package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersistence;
	private PersonOutputPort personPersistence;
	private ProfessionOutputPort professionPersistence;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersistence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersistence) {
		this.studyPersistence = studyPersistence;
		this.personPersistence = personPersistence;
		this.professionPersistence = professionPersistence;
	}

	@Override
	public void setPersistence(StudyOutputPort studyPersistence, PersonOutputPort personPersistence,
			ProfessionOutputPort professionPersistence) {
		this.studyPersistence = studyPersistence;
		this.personPersistence = personPersistence;
		this.professionPersistence = professionPersistence;
	}

	@Override
	public Study create(Study study) throws AlreadyExistException, NoExistException, InvalidDataException {
		log.debug("Into create study on Application Domain");
		validate(study);
		Integer personId = study.getPerson().getIdentification();
		Integer professionId = study.getProfession().getIdentification();
		study.setPerson(findPerson(personId));
		study.setProfession(findProfession(professionId));
		if (studyPersistence.findById(personId, professionId) != null) {
			throw new AlreadyExistException("The study of person " + personId + " in profession " + professionId
					+ " already exists into db, cannot be created");
		}
		return studyPersistence.save(study);
	}

	@Override
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException, InvalidDataException {
		if (studyPersistence.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be edited");
		}
		// La llave compuesta (persona, profesion) no cambia al editar: solo fecha y universidad
		study.setPerson(findPerson(personIdentification));
		study.setProfession(findProfession(professionIdentification));
		validate(study);
		return studyPersistence.save(study);
	}

	@Override
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		if (studyPersistence.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be dropped");
		}
		return studyPersistence.delete(personIdentification, professionIdentification);
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersistence.getClass());
		return studyPersistence.find();
	}

	@Override
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study study = studyPersistence.findById(personIdentification, professionIdentification);
		if (study == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be found");
		}
		return study;
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	private String notFound(Integer personId, Integer professionId) {
		return "The study of person " + personId + " in profession " + professionId + " does not exist into db";
	}

	/** Regla de negocio: la persona del estudio debe existir. */
	private Person findPerson(Integer personId) throws NoExistException {
		Person person = personPersistence.findById(personId);
		if (person == null) {
			throw new NoExistException("The person with id " + personId + " does not exist into db");
		}
		return person;
	}

	/** Regla de negocio: la profesion del estudio debe existir. */
	private Profession findProfession(Integer professionId) throws NoExistException {
		Profession profession = professionPersistence.findById(professionId);
		if (profession == null) {
			throw new NoExistException("The profession with id " + professionId + " does not exist into db");
		}
		return profession;
	}

	private void validate(Study study) throws InvalidDataException {
		if (study == null) {
			throw new InvalidDataException("The study is required");
		}
		if (study.getPerson() == null || study.getPerson().getIdentification() == null) {
			throw new InvalidDataException("The person id of the study is required");
		}
		if (study.getProfession() == null || study.getProfession().getIdentification() == null) {
			throw new InvalidDataException("The profession id of the study is required");
		}
		if (!study.isValidGraduationDate()) {
			throw new InvalidDataException("The graduation date cannot be in the future");
		}
		if (!study.isValidUniversityName()) {
			throw new InvalidDataException("The university name has max 50 characters");
		}
	}
}

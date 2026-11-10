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

/**
 * Caso de uso de estudios: valida que la persona y la profesión existan en el
 * mismo motor y que la fecha de grado no esté en el futuro.
 */
@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	/** Adaptador de salida de estudios. */
	private StudyOutputPort studyPersistence;

	/** Adaptador de salida de personas, usado para validar la persona del estudio. */
	private PersonOutputPort personPersistence;

	/** Adaptador de salida de profesiones, usado para validar la profesión del estudio. */
	private ProfessionOutputPort professionPersistence;

	/**
	 * Crea el caso de uso con los adaptadores de salida indicados. Por defecto Spring
	 * inyecta los de MariaDB.
	 *
	 * @param studyPersistence adaptador de salida de estudios
	 * @param personPersistence adaptador de salida de personas del mismo motor
	 * @param professionPersistence adaptador de salida de profesiones del mismo motor
	 */
	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersistence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersistence) {
		this.studyPersistence = studyPersistence;
		this.personPersistence = personPersistence;
		this.professionPersistence = professionPersistence;
	}

	/**
	 * Cambia los adaptadores de salida.
	 *
	 * @param studyPersistence adaptador de salida de estudios
	 * @param personPersistence adaptador de salida de personas del mismo motor
	 * @param professionPersistence adaptador de salida de profesiones del mismo motor
	 */
	@Override
	public void setPersistence(StudyOutputPort studyPersistence, PersonOutputPort personPersistence,
			ProfessionOutputPort professionPersistence) {
		this.studyPersistence = studyPersistence;
		this.personPersistence = personPersistence;
		this.professionPersistence = professionPersistence;
	}

	/**
	 * Valida el estudio, carga la persona y la profesión, y lo registra si no existe.
	 *
	 * @param study datos del estudio
	 * @return el estudio guardado con los datos de la persona y la profesión
	 * @throws AlreadyExistException si el estudio ya está registrado
	 * @throws NoExistException si la persona o la profesión no existen
	 * @throws InvalidDataException si los datos no son válidos
	 */
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

	/**
	 * Actualiza la fecha de grado y la universidad de un estudio existente. La llave
	 * compuesta se toma de los parámetros.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @param study datos nuevos
	 * @return el estudio actualizado
	 * @throws NoExistException si el estudio no existe
	 * @throws InvalidDataException si los datos nuevos no son válidos
	 */
	@Override
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException, InvalidDataException {
		if (studyPersistence.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be edited");
		}
		// La llave compuesta se conserva; solo cambian la fecha y la universidad
		study.setPerson(findPerson(personIdentification));
		study.setProfession(findProfession(professionIdentification));
		validate(study);
		return studyPersistence.save(study);
	}

	/**
	 * Elimina el estudio si existe.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return {@code true} si quedó eliminado
	 * @throws NoExistException si el estudio no existe
	 */
	@Override
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		if (studyPersistence.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be dropped");
		}
		return studyPersistence.delete(personIdentification, professionIdentification);
	}

	/**
	 * Consulta todos los estudios del motor configurado.
	 *
	 * @return lista de estudios
	 */
	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersistence.getClass());
		return studyPersistence.find();
	}

	/**
	 * Busca un estudio por su llave compuesta.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return el estudio encontrado
	 * @throws NoExistException si el estudio no existe
	 */
	@Override
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study study = studyPersistence.findById(personIdentification, professionIdentification);
		if (study == null) {
			throw new NoExistException(notFound(personIdentification, professionIdentification) + ", cannot be found");
		}
		return study;
	}

	/**
	 * Cuenta los estudios del motor configurado.
	 *
	 * @return número de estudios
	 */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/**
	 * Arma el mensaje de error para un estudio que no existe.
	 *
	 * @param personId cédula de la persona
	 * @param professionId identificador de la profesión
	 * @return texto del mensaje
	 */
	private String notFound(Integer personId, Integer professionId) {
		return "The study of person " + personId + " in profession " + professionId + " does not exist into db";
	}

	/**
	 * Busca en el motor configurado la persona del estudio.
	 *
	 * @param personId cédula de la persona
	 * @return la persona encontrada
	 * @throws NoExistException si la persona no existe
	 */
	private Person findPerson(Integer personId) throws NoExistException {
		Person person = personPersistence.findById(personId);
		if (person == null) {
			throw new NoExistException("The person with id " + personId + " does not exist into db");
		}
		return person;
	}

	/**
	 * Busca en el motor configurado la profesión del estudio.
	 *
	 * @param professionId identificador de la profesión
	 * @return la profesión encontrada
	 * @throws NoExistException si la profesión no existe
	 */
	private Profession findProfession(Integer professionId) throws NoExistException {
		Profession profession = professionPersistence.findById(professionId);
		if (profession == null) {
			throw new NoExistException("The profession with id " + professionId + " does not exist into db");
		}
		return profession;
	}

	/**
	 * Aplica las reglas de negocio de un estudio: persona y profesión informadas,
	 * fecha de grado no futura y universidad de máximo 50 caracteres.
	 *
	 * @param study estudio a validar
	 * @throws InvalidDataException con el motivo de la primera regla que no se cumple
	 */
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

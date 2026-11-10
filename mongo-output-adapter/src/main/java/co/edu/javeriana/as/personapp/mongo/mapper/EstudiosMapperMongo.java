package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

/**
 * Convierte estudios entre el dominio ({@link Study}) y el documento de MongoDB
 * ({@link EstudiosDocument}).
 */
@Mapper
public class EstudiosMapperMongo {

	/**
	 * Arma el {@code _id} de un estudio. MongoDB no tiene llaves compuestas, así que
	 * la pareja persona y profesión se guarda como el texto {@code cedula-idProfesion}.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return identificador del documento, por ejemplo {@code 123456789-1}
	 */
	public static String buildId(Integer personIdentification, Integer professionIdentification) {
		return personIdentification + "-" + professionIdentification;
	}

	/**
	 * Convierte un estudio del dominio en documento.
	 *
	 * @param study estudio del dominio
	 * @return documento listo para guardar
	 */
	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(buildId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
		estudio.setPrimaryPersona(validatePrimaryPersona(study.getPerson()));
		estudio.setPrimaryProfesion(validatePrimaryProfesion(study.getProfession()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	/**
	 * Crea la referencia a la persona con solo su cédula; la referencia guarda
	 * únicamente el {@code _id} del documento.
	 *
	 * @param person persona del estudio
	 * @return documento de persona con solo el identificador
	 */
	private PersonaDocument validatePrimaryPersona(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		return personaDocument;
	}

	/**
	 * Crea la referencia a la profesión con solo su identificador; la referencia guarda
	 * únicamente el {@code _id} del documento.
	 *
	 * @param profession profesión del estudio
	 * @return documento de profesión con solo el identificador
	 */
	private ProfesionDocument validatePrimaryProfesion(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		return profesionDocument;
	}

	/**
	 * Normaliza el nombre de la universidad antes de guardarlo.
	 *
	 * @param universityName universidad del dominio
	 * @return la universidad, o cadena vacía si es nula
	 */
	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	/**
	 * Convierte un documento en estudio del dominio. La persona y la profesión se
	 * convierten sin sus listas para no recorrer las relaciones en ciclo.
	 *
	 * @param estudiosDocument documento leído de MongoDB
	 * @return estudio del dominio
	 */
	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosDocument.getPrimaryPersona()));
		study.setProfession(validateProfession(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(estudiosDocument.getFecha());
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		return study;
	}

	/**
	 * Convierte la persona referenciada sin sus listas.
	 *
	 * @param persona documento de la persona; puede ser nulo
	 * @return persona del dominio
	 */
	private Person validatePerson(PersonaDocument persona) {
		return persona != null ? PersonaMapperMongo.toDomainBasic(persona) : new Person();
	}

	/**
	 * Convierte la profesión referenciada sin sus estudios.
	 *
	 * @param profesion documento de la profesión; puede ser nulo
	 * @return profesión del dominio
	 */
	private Profession validateProfession(ProfesionDocument profesion) {
		return profesion != null ? ProfesionMapperMongo.toDomainBasic(profesion) : new Profession();
	}

	/**
	 * Normaliza la universidad leída de la base de datos.
	 *
	 * @param univer valor del documento
	 * @return la universidad, o cadena vacía si es nula
	 */
	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}

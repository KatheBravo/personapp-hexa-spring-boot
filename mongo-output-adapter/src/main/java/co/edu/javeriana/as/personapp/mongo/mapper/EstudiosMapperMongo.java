package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

@Mapper
public class EstudiosMapperMongo {

	/**
	 * En MongoDB no hay llave compuesta: el _id del estudio se arma como
	 * "cedula-idProfesion" (por ejemplo "123456789-1").
	 */
	public static String buildId(Integer personIdentification, Integer professionIdentification) {
		return personIdentification + "-" + professionIdentification;
	}

	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(buildId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
		estudio.setPrimaryPersona(validatePrimaryPersona(study.getPerson()));
		estudio.setPrimaryProfesion(validatePrimaryProfesion(study.getProfession()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	/** Solo la llave: @DocumentReference guarda unicamente el _id de la persona. */
	private PersonaDocument validatePrimaryPersona(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		return personaDocument;
	}

	/** Solo la llave: @DocumentReference guarda unicamente el _id de la profesion. */
	private ProfesionDocument validatePrimaryProfesion(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		return profesionDocument;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosDocument.getPrimaryPersona()));
		study.setProfession(validateProfession(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(estudiosDocument.getFecha());
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		// ERROR DE LA PLANTILLA: aqui se retornaba null
		return study;
	}

	private Person validatePerson(PersonaDocument persona) {
		return persona != null ? PersonaMapperMongo.toDomainBasic(persona) : new Person();
	}

	private Profession validateProfession(ProfesionDocument profesion) {
		return profesion != null ? ProfesionMapperMongo.toDomainBasic(profesion) : new Profession();
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}

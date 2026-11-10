package co.edu.javeriana.as.personapp.mariadb.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class EstudiosMapperMaria {

	public EstudiosEntity fromDomainToAdapter(Study study) {
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		// Referencias por llave; el adaptador las reemplaza por las entidades reales
		estudio.setPersona(new PersonaEntity(study.getPerson().getIdentification()));
		estudio.setProfesion(new ProfesionEntity(study.getProfession().getIdentification()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosEntity));
		study.setProfession(validateProfession(estudiosEntity));
		study.setGraduationDate(estudiosEntity.getFecha());
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		// ERROR DE LA PLANTILLA: aqui se retornaba null y toda persona con estudios fallaba
		return study;
	}

	/** Persona "basica" para no entrar en recursion persona -> estudio -> persona. */
	private Person validatePerson(EstudiosEntity estudiosEntity) {
		if (estudiosEntity.getPersona() != null) {
			return PersonaMapperMaria.toDomainBasic(estudiosEntity.getPersona());
		}
		Person person = new Person();
		person.setIdentification(estudiosEntity.getEstudiosPK().getCcPer());
		return person;
	}

	/** Profesion "basica" para no entrar en recursion profesion -> estudio -> profesion. */
	private Profession validateProfession(EstudiosEntity estudiosEntity) {
		if (estudiosEntity.getProfesion() != null) {
			return ProfesionMapperMaria.toDomainBasic(estudiosEntity.getProfesion());
		}
		Profession profession = new Profession();
		profession.setIdentification(estudiosEntity.getEstudiosPK().getIdProf());
		return profession;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}

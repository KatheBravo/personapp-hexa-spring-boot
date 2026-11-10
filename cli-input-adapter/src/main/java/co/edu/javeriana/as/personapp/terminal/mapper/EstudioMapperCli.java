package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioModelCli(person != null ? person.getIdentification() : null,
				person != null ? fullName(person) : null, profession != null ? profession.getIdentification() : null,
				profession != null ? profession.getName() : null, study.getGraduationDate(),
				study.getUniversityName());
	}

	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/** Persona y profesion van solo con su llave; el caso de uso verifica que existan. */
	public Study fromAdapterCliToDomain(EstudioModelCli estudioModelCli) {
		Study study = new Study();
		Person person = new Person();
		person.setIdentification(estudioModelCli.getCcPersona());
		Profession profession = new Profession();
		profession.setIdentification(estudioModelCli.getIdProfesion());
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(estudioModelCli.getFechaGrado());
		study.setUniversityName(estudioModelCli.getUniversidad());
		return study;
	}
}

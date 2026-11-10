package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study, String database) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioResponse(
				person != null ? RestValues.text(person.getIdentification()) : null,
				person != null ? fullName(person) : null,
				profession != null ? RestValues.text(profession.getIdentification()) : null,
				profession != null ? profession.getName() : null,
				RestValues.text(study.getGraduationDate()),
				study.getUniversityName(),
				database,
				"OK");
	}

	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/** Persona y profesion llegan solo con su llave; el caso de uso verifica que existan. */
	public Study fromAdapterToDomain(EstudioRequest request) throws InvalidDataException {
		Study study = new Study();
		Person person = new Person();
		person.setIdentification(RestValues.requiredInteger(request.getPersonId(), "personId"));
		Profession profession = new Profession();
		profession.setIdentification(RestValues.requiredInteger(request.getProfessionId(), "professionId"));
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(RestValues.optionalDate(request.getGraduationDate(), "graduationDate"));
		study.setUniversityName(RestValues.optionalText(request.getUniversityName()));
		return study;
	}
}

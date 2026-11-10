package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

/**
 * Convierte estudios entre el dominio y los modelos de la API REST.
 */
@Mapper
public class EstudioMapperRest {

	/**
	 * Convierte un estudio del dominio en respuesta REST.
	 *
	 * @param study estudio del dominio
	 * @param database nombre del motor que atendió la operación
	 * @return respuesta con los datos del estudio, de la persona y de la profesión
	 */
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

	/**
	 * Une el nombre y el apellido de una persona.
	 *
	 * @param person persona
	 * @return nombre completo, sin espacios sobrantes
	 */
	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/**
	 * Convierte la petición REST en estudio del dominio. La persona y la profesión
	 * quedan solo con su identificador; el caso de uso verifica que existan.
	 *
	 * @param request datos recibidos en el cuerpo
	 * @return estudio del dominio
	 * @throws InvalidDataException si falta un identificador, no es numérico o la fecha no
	 *         tiene el formato yyyy-MM-dd
	 */
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

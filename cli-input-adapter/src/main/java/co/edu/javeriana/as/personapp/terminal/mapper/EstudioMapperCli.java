package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

/**
 * Convierte estudios entre el dominio y el modelo que se muestra en consola.
 */
@Mapper
public class EstudioMapperCli {

	/**
	 * Convierte un estudio del dominio en el modelo de consola.
	 *
	 * @param study estudio del dominio
	 * @return modelo con la llave, los nombres de la persona y la profesión, la fecha y
	 *         la universidad
	 */
	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioModelCli(person != null ? person.getIdentification() : null,
				person != null ? fullName(person) : null, profession != null ? profession.getIdentification() : null,
				profession != null ? profession.getName() : null, study.getGraduationDate(),
				study.getUniversityName());
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
	 * Convierte los datos leídos de la consola en estudio del dominio. La persona y la
	 * profesión quedan solo con su identificador; el caso de uso verifica que existan.
	 *
	 * @param estudioModelCli datos leídos de la consola
	 * @return estudio del dominio
	 */
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

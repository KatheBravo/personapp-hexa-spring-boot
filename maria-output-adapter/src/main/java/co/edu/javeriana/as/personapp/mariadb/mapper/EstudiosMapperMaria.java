package co.edu.javeriana.as.personapp.mariadb.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

/**
 * Convierte estudios entre el dominio ({@link Study}) y la entidad JPA
 * ({@link EstudiosEntity}).
 */
@Mapper
public class EstudiosMapperMaria {

	/**
	 * Convierte un estudio del dominio en entidad. La persona y la profesión quedan
	 * como referencias con solo su llave; el adaptador las reemplaza por las
	 * entidades administradas.
	 *
	 * @param study estudio del dominio
	 * @return entidad lista para guardar
	 */
	public EstudiosEntity fromDomainToAdapter(Study study) {
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		estudio.setPersona(new PersonaEntity(study.getPerson().getIdentification()));
		estudio.setProfesion(new ProfesionEntity(study.getProfession().getIdentification()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
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
	 * Convierte una entidad en estudio del dominio. La persona y la profesión se
	 * convierten sin sus listas para no recorrer las relaciones en ciclo.
	 *
	 * @param estudiosEntity entidad leída de MariaDB
	 * @return estudio del dominio
	 */
	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosEntity));
		study.setProfession(validateProfession(estudiosEntity));
		study.setGraduationDate(estudiosEntity.getFecha());
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study;
	}

	/**
	 * Obtiene la persona del estudio sin sus listas. Si la relación no está cargada,
	 * crea una persona solo con la cédula de la llave.
	 *
	 * @param estudiosEntity entidad del estudio
	 * @return persona del estudio
	 */
	private Person validatePerson(EstudiosEntity estudiosEntity) {
		if (estudiosEntity.getPersona() != null) {
			return PersonaMapperMaria.toDomainBasic(estudiosEntity.getPersona());
		}
		Person person = new Person();
		person.setIdentification(estudiosEntity.getEstudiosPK().getCcPer());
		return person;
	}

	/**
	 * Obtiene la profesión del estudio sin sus estudios. Si la relación no está
	 * cargada, crea una profesión solo con el identificador de la llave.
	 *
	 * @param estudiosEntity entidad del estudio
	 * @return profesión del estudio
	 */
	private Profession validateProfession(EstudiosEntity estudiosEntity) {
		if (estudiosEntity.getProfesion() != null) {
			return ProfesionMapperMaria.toDomainBasic(estudiosEntity.getProfesion());
		}
		Profession profession = new Profession();
		profession.setIdentification(estudiosEntity.getEstudiosPK().getIdProf());
		return profession;
	}

	/**
	 * Normaliza la universidad leída de la base de datos.
	 *
	 * @param univer valor de la columna
	 * @return la universidad, o cadena vacía si es nula
	 */
	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}

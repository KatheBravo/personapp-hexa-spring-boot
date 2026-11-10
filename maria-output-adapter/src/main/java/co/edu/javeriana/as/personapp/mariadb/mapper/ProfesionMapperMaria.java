package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import lombok.RequiredArgsConstructor;

/**
 * Convierte profesiones entre el dominio ({@link Profession}) y la entidad JPA
 * ({@link ProfesionEntity}).
 */
@Mapper
@RequiredArgsConstructor
public class ProfesionMapperMaria {

	/** Conversor de los estudios de la profesión. */
	private final EstudiosMapperMaria estudiosMapperMaria;

	/**
	 * Convierte una profesión del dominio en una entidad nueva, con la lista de
	 * estudios vacía.
	 *
	 * @param profession profesión del dominio
	 * @return entidad lista para guardar
	 */
	public ProfesionEntity fromDomainToAdapter(Profession profession) {
		ProfesionEntity profesionEntity = new ProfesionEntity();
		copyToAdapter(profession, profesionEntity);
		profesionEntity.setEstudios(new ArrayList<EstudiosEntity>());
		return profesionEntity;
	}

	/**
	 * Copia los datos editables de la profesión sobre una entidad, sin modificar sus
	 * estudios.
	 *
	 * @param profession profesión del dominio con los datos nuevos
	 * @param profesionEntity entidad que recibe los datos
	 * @return la misma entidad, actualizada
	 */
	public ProfesionEntity copyToAdapter(Profession profession, ProfesionEntity profesionEntity) {
		profesionEntity.setId(profession.getIdentification());
		profesionEntity.setNom(profession.getName());
		profesionEntity.setDes(validateDes(profession.getDescription()));
		return profesionEntity;
	}

	/**
	 * Normaliza la descripción antes de guardarla.
	 *
	 * @param description descripción del dominio
	 * @return la descripción, o cadena vacía si es nula
	 */
	private static String validateDes(String description) {
		return description != null ? description : "";
	}

	/**
	 * Convierte una entidad en profesión del dominio, incluidos sus estudios.
	 *
	 * @param profesionEntity entidad leída de MariaDB
	 * @return profesión del dominio completa
	 */
	public Profession fromAdapterToDomain(ProfesionEntity profesionEntity) {
		Profession profession = toDomainBasic(profesionEntity);
		profession.setStudies(validateStudies(profesionEntity.getEstudios()));
		return profession;
	}

	/**
	 * Convierte una entidad en profesión del dominio sin sus estudios. La usa el
	 * mapper de estudios.
	 *
	 * @param profesionEntity entidad leída de MariaDB
	 * @return profesión con la lista de estudios vacía
	 */
	public static Profession toDomainBasic(ProfesionEntity profesionEntity) {
		Profession profession = new Profession();
		profession.setIdentification(profesionEntity.getId());
		if (profesionEntity.getNom() != null) {
			profession.setName(profesionEntity.getNom());
		}
		profession.setDescription(validateDescription(profesionEntity.getDes()));
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}

	/**
	 * Normaliza la descripción leída de la base de datos.
	 *
	 * @param des valor de la columna
	 * @return la descripción, o cadena vacía si es nula
	 */
	private static String validateDescription(String des) {
		return des != null ? des : "";
	}

	/**
	 * Convierte los estudios de la profesión.
	 *
	 * @param estudiosEntity estudios leídos de MariaDB
	 * @return estudios del dominio; lista vacía si no tiene
	 */
	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}

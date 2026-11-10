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

@Mapper
@RequiredArgsConstructor
public class ProfesionMapperMaria {

	private final EstudiosMapperMaria estudiosMapperMaria;

	/** Solo los datos propios de la profesion; los estudios los guarda su adaptador. */
	public ProfesionEntity fromDomainToAdapter(Profession profession) {
		ProfesionEntity profesionEntity = new ProfesionEntity();
		copyToAdapter(profession, profesionEntity);
		profesionEntity.setEstudios(new ArrayList<EstudiosEntity>());
		return profesionEntity;
	}

	/** Copia los datos editables sobre una entidad existente sin tocar sus estudios. */
	public ProfesionEntity copyToAdapter(Profession profession, ProfesionEntity profesionEntity) {
		profesionEntity.setId(profession.getIdentification());
		profesionEntity.setNom(profession.getName());
		profesionEntity.setDes(validateDes(profession.getDescription()));
		return profesionEntity;
	}

	private static String validateDes(String description) {
		return description != null ? description : "";
	}

	/** Profesion completa: con los estudios que la referencian. */
	public Profession fromAdapterToDomain(ProfesionEntity profesionEntity) {
		Profession profession = toDomainBasic(profesionEntity);
		profession.setStudies(validateStudies(profesionEntity.getEstudios()));
		return profession;
	}

	/** Profesion sin estudios, para cortar el ciclo profesion -> estudio -> profesion. */
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

	private static String validateDescription(String des) {
		return des != null ? des : "";
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}

package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import lombok.RequiredArgsConstructor;

@Mapper
@RequiredArgsConstructor
public class ProfesionMapperMongo {

	private final EstudiosMapperMongo estudiosMapperMongo;

	/** Solo los datos propios; los estudios son @ReadOnlyProperty (referencia inversa). */
	public ProfesionDocument fromDomainToAdapter(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(validateDes(profession.getDescription()));
		return profesionDocument;
	}

	private static String validateDes(String description) {
		return description != null ? description : "";
	}

	/** Profesion completa: con los estudios que la referencian. */
	public Profession fromAdapterToDomain(ProfesionDocument profesionDocument) {
		Profession profession = toDomainBasic(profesionDocument);
		profession.setStudies(validateStudies(profesionDocument.getEstudios()));
		return profession;
	}

	/** Profesion sin estudios (corta el ciclo profesion -> estudio -> profesion). */
	public static Profession toDomainBasic(ProfesionDocument profesionDocument) {
		Profession profession = new Profession();
		profession.setIdentification(profesionDocument.getId());
		if (profesionDocument.getNom() != null) {
			profession.setName(profesionDocument.getNom());
		}
		profession.setDescription(validateDescription(profesionDocument.getDes()));
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}

	private static String validateDescription(String des) {
		return des != null ? des : "";
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocument) {
		return estudiosDocument != null && !estudiosDocument.isEmpty() ? estudiosDocument.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}

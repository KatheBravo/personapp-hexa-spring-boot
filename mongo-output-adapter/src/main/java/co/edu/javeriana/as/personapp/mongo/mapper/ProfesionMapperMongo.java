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

/**
 * Convierte profesiones entre el dominio ({@link Profession}) y el documento de
 * MongoDB ({@link ProfesionDocument}).
 */
@Mapper
@RequiredArgsConstructor
public class ProfesionMapperMongo {

	/** Conversor de los estudios de la profesión. */
	private final EstudiosMapperMongo estudiosMapperMongo;

	/**
	 * Convierte una profesión del dominio en documento. Los estudios no se incluyen:
	 * son de solo lectura y se guardan en su colección.
	 *
	 * @param profession profesión del dominio
	 * @return documento listo para guardar
	 */
	public ProfesionDocument fromDomainToAdapter(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(validateDes(profession.getDescription()));
		return profesionDocument;
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
	 * Convierte un documento en profesión del dominio, incluidos sus estudios.
	 *
	 * @param profesionDocument documento leído de MongoDB
	 * @return profesión del dominio completa
	 */
	public Profession fromAdapterToDomain(ProfesionDocument profesionDocument) {
		Profession profession = toDomainBasic(profesionDocument);
		profession.setStudies(validateStudies(profesionDocument.getEstudios()));
		return profession;
	}

	/**
	 * Convierte un documento en profesión del dominio sin sus estudios. La usa el
	 * mapper de estudios.
	 *
	 * @param profesionDocument documento leído de MongoDB
	 * @return profesión con la lista de estudios vacía
	 */
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

	/**
	 * Normaliza la descripción leída de la base de datos.
	 *
	 * @param des valor del documento
	 * @return la descripción, o cadena vacía si es nula
	 */
	private static String validateDescription(String des) {
		return des != null ? des : "";
	}

	/**
	 * Convierte los estudios de la profesión.
	 *
	 * @param estudiosDocument estudios leídos de MongoDB
	 * @return estudios del dominio; lista vacía si no tiene
	 */
	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocument) {
		return estudiosDocument != null && !estudiosDocument.isEmpty() ? estudiosDocument.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}

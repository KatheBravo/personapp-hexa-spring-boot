package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import lombok.RequiredArgsConstructor;

/**
 * Convierte personas entre el dominio ({@link Person}) y el documento de MongoDB
 * ({@link PersonaDocument}).
 *
 * Los mappers de teléfono y estudio usan {@link #toDomainBasic(PersonaDocument)},
 * que convierte la persona sin sus listas, para no recorrer la relación en ciclo.
 */
@Mapper
@RequiredArgsConstructor
public class PersonaMapperMongo {

	/** Conversor de los estudios de la persona. */
	private final EstudiosMapperMongo estudiosMapperMongo;

	/** Conversor de los teléfonos de la persona. */
	private final TelefonoMapperMongo telefonoMapperMongo;

	/**
	 * Convierte una persona del dominio en documento. Solo incluye los datos
	 * propios: los teléfonos y estudios son de solo lectura y se guardan en sus
	 * colecciones.
	 *
	 * @param person persona del dominio
	 * @return documento listo para guardar
	 */
	public PersonaDocument fromDomainToAdapter(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		personaDocument.setNombre(person.getFirstName());
		personaDocument.setApellido(person.getLastName());
		personaDocument.setGenero(validateGenero(person.getGender()));
		personaDocument.setEdad(validateEdad(person.getAge()));
		return personaDocument;
	}

	/**
	 * Convierte el género del dominio al valor del documento.
	 *
	 * @param gender género del dominio
	 * @return {@code "F"}, {@code "M"} o un espacio si el género es otro
	 */
	private static String validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M" : " ";
	}

	/**
	 * Normaliza la edad antes de guardarla.
	 *
	 * @param age edad del dominio
	 * @return la edad, o {@code null} si es nula o negativa
	 */
	private static Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	/**
	 * Convierte un documento en persona del dominio, incluidos sus teléfonos y
	 * estudios.
	 *
	 * @param personaDocument documento leído de MongoDB
	 * @return persona del dominio completa
	 */
	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		Person person = toDomainBasic(personaDocument);
		person.setStudies(validateStudies(personaDocument.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaDocument.getTelefonos()));
		return person;
	}

	/**
	 * Convierte un documento en persona del dominio sin teléfonos ni estudios. La
	 * usan los mappers de teléfono y estudio.
	 *
	 * @param personaDocument documento leído de MongoDB
	 * @return persona con las listas vacías
	 */
	public static Person toDomainBasic(PersonaDocument personaDocument) {
		Person person = new Person();
		person.setIdentification(personaDocument.getId());
		if (personaDocument.getNombre() != null) {
			person.setFirstName(personaDocument.getNombre());
		}
		if (personaDocument.getApellido() != null) {
			person.setLastName(personaDocument.getApellido());
		}
		person.setGender(validateGender(personaDocument.getGenero()));
		person.setAge(validateAge(personaDocument.getEdad()));
		person.setStudies(new ArrayList<Study>());
		person.setPhoneNumbers(new ArrayList<Phone>());
		return person;
	}

	/**
	 * Convierte el valor del documento al género del dominio.
	 *
	 * @param genero valor del documento; puede ser nulo
	 * @return {@link Gender#FEMALE}, {@link Gender#MALE} u {@link Gender#OTHER}
	 */
	private static Gender validateGender(String genero) {
		return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	/**
	 * Normaliza la edad leída de la base de datos.
	 *
	 * @param edad valor del documento
	 * @return la edad, o {@code null} si es nula o negativa
	 */
	private static Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	/**
	 * Convierte los estudios de la persona.
	 *
	 * @param estudiosDocuments estudios leídos de MongoDB
	 * @return estudios del dominio; lista vacía si no tiene
	 */
	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null && !estudiosDocuments.isEmpty() ? estudiosDocuments.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	/**
	 * Convierte los teléfonos de la persona.
	 *
	 * @param telefonosDocuments teléfonos leídos de MongoDB
	 * @return teléfonos del dominio; lista vacía si no tiene
	 */
	private List<Phone> validatePhones(List<TelefonoDocument> telefonosDocuments) {
		return telefonosDocuments != null && !telefonosDocuments.isEmpty() ? telefonosDocuments.stream()
				.map(telefono -> telefonoMapperMongo.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}

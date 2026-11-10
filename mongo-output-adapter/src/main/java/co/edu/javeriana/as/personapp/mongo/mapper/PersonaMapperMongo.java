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
 * Convierte entre Person (dominio) y PersonaDocument (adaptador MongoDB).
 *
 * Igual que en MariaDB, los mappers de telefono y estudios usan la persona
 * "basica" ({@link #toDomainBasic(PersonaDocument)}) para no caer en la
 * recursion infinita persona -> telefono -> persona -> ... de la plantilla.
 */
@Mapper
@RequiredArgsConstructor
public class PersonaMapperMongo {

	private final EstudiosMapperMongo estudiosMapperMongo;

	private final TelefonoMapperMongo telefonoMapperMongo;

	/**
	 * Solo los datos propios. Telefonos y estudios son @ReadOnlyProperty en el
	 * documento (se consultan por referencia inversa), no se guardan aqui.
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

	private static String validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M" : " ";
	}

	private static Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	/** Persona completa: con sus telefonos y estudios. */
	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		Person person = toDomainBasic(personaDocument);
		person.setStudies(validateStudies(personaDocument.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaDocument.getTelefonos()));
		return person;
	}

	/** Persona sin relaciones (corta el ciclo persona -> telefono/estudio -> persona). */
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

	private static Gender validateGender(String genero) {
		return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private static Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null && !estudiosDocuments.isEmpty() ? estudiosDocuments.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoDocument> telefonosDocuments) {
		return telefonosDocuments != null && !telefonosDocuments.isEmpty() ? telefonosDocuments.stream()
				.map(telefono -> telefonoMapperMongo.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}

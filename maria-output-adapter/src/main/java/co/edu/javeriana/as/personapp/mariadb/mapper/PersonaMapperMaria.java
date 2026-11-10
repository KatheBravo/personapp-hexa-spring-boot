package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.RequiredArgsConstructor;

/**
 * Convierte entre la entidad de negocio Person (ingles = dominio) y la entidad
 * de datos PersonaEntity (espanol = adaptador).
 *
 * Correccion respecto a la plantilla: la persona tiene telefonos y estudios, y
 * cada telefono/estudio vuelve a tener a su persona. Si el mapper de telefono
 * llama al mapper "completo" de persona se produce una recursion infinita
 * (StackOverflowError). Por eso existe {@link #toDomainBasic(PersonaEntity)}:
 * la persona "basica" (sin listas) que usan los mappers hijos.
 */
@Mapper
@RequiredArgsConstructor
public class PersonaMapperMaria {

	private final EstudiosMapperMaria estudiosMapperMaria;

	private final TelefonoMapperMaria telefonoMapperMaria;

	/**
	 * Solo los datos propios de la persona. Los telefonos y estudios los
	 * guardan sus propios adaptadores, por eso las listas van vacias.
	 */
	public PersonaEntity fromDomainToAdapter(Person person) {
		PersonaEntity personaEntity = new PersonaEntity();
		copyToAdapter(person, personaEntity);
		personaEntity.setEstudios(new ArrayList<EstudiosEntity>());
		personaEntity.setTelefonos(new ArrayList<TelefonoEntity>());
		return personaEntity;
	}

	/**
	 * Copia los datos editables sobre una entidad que ya existe, sin tocar sus
	 * relaciones (telefonos y estudios).
	 */
	public PersonaEntity copyToAdapter(Person person, PersonaEntity personaEntity) {
		personaEntity.setCc(person.getIdentification());
		personaEntity.setNombre(person.getFirstName());
		personaEntity.setApellido(person.getLastName());
		personaEntity.setGenero(validateGenero(person.getGender()));
		personaEntity.setEdad(validateEdad(person.getAge()));
		return personaEntity;
	}

	private static Character validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? 'F' : gender == Gender.MALE ? 'M' : null;
	}

	private static Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	/** Persona completa: con sus telefonos y estudios. */
	public Person fromAdapterToDomain(PersonaEntity personaEntity) {
		Person person = toDomainBasic(personaEntity);
		person.setStudies(validateStudies(personaEntity.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaEntity.getTelefonos()));
		return person;
	}

	/**
	 * Persona sin relaciones. La usan los mappers de telefono y estudios para
	 * cortar el ciclo persona -> telefono -> persona -> ...
	 */
	public static Person toDomainBasic(PersonaEntity personaEntity) {
		Person person = new Person();
		person.setIdentification(personaEntity.getCc());
		if (personaEntity.getNombre() != null) {
			person.setFirstName(personaEntity.getNombre());
		}
		if (personaEntity.getApellido() != null) {
			person.setLastName(personaEntity.getApellido());
		}
		person.setGender(validateGender(personaEntity.getGenero()));
		person.setAge(validateAge(personaEntity.getEdad()));
		person.setStudies(new ArrayList<Study>());
		person.setPhoneNumbers(new ArrayList<Phone>());
		return person;
	}

	private static Gender validateGender(Character genero) {
		// Character.valueOf(...).equals evita el NullPointerException del unboxing de "genero == 'F'"
		return Character.valueOf('F').equals(genero) ? Gender.FEMALE
				: Character.valueOf('M').equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private static Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoEntity> telefonoEntities) {
		return telefonoEntities != null && !telefonoEntities.isEmpty() ? telefonoEntities.stream()
				.map(telefono -> telefonoMapperMaria.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}

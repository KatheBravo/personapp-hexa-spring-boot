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
 * Convierte personas entre el dominio ({@link Person}) y la entidad JPA
 * ({@link PersonaEntity}).
 *
 * Una persona tiene teléfonos y estudios, y cada uno vuelve a apuntar a la
 * persona. Para no recorrer ese ciclo, los mappers de teléfono y estudio usan
 * {@link #toDomainBasic(PersonaEntity)}, que convierte la persona sin sus listas.
 */
@Mapper
@RequiredArgsConstructor
public class PersonaMapperMaria {

	/** Conversor de los estudios de la persona. */
	private final EstudiosMapperMaria estudiosMapperMaria;

	/** Conversor de los teléfonos de la persona. */
	private final TelefonoMapperMaria telefonoMapperMaria;

	/**
	 * Convierte una persona del dominio en una entidad nueva. Las listas de
	 * teléfonos y estudios quedan vacías porque cada una la guarda su adaptador.
	 *
	 * @param person persona del dominio
	 * @return entidad lista para guardar
	 */
	public PersonaEntity fromDomainToAdapter(Person person) {
		PersonaEntity personaEntity = new PersonaEntity();
		copyToAdapter(person, personaEntity);
		personaEntity.setEstudios(new ArrayList<EstudiosEntity>());
		personaEntity.setTelefonos(new ArrayList<TelefonoEntity>());
		return personaEntity;
	}

	/**
	 * Copia los datos editables de la persona sobre una entidad, sin modificar sus
	 * teléfonos ni estudios.
	 *
	 * @param person persona del dominio con los datos nuevos
	 * @param personaEntity entidad que recibe los datos
	 * @return la misma entidad, actualizada
	 */
	public PersonaEntity copyToAdapter(Person person, PersonaEntity personaEntity) {
		personaEntity.setCc(person.getIdentification());
		personaEntity.setNombre(person.getFirstName());
		personaEntity.setApellido(person.getLastName());
		personaEntity.setGenero(validateGenero(person.getGender()));
		personaEntity.setEdad(validateEdad(person.getAge()));
		return personaEntity;
	}

	/**
	 * Convierte el género del dominio al valor de la columna.
	 *
	 * @param gender género del dominio
	 * @return {@code 'F'}, {@code 'M'} o {@code null} si el género es otro
	 */
	private static Character validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? 'F' : gender == Gender.MALE ? 'M' : null;
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
	 * Convierte una entidad en persona del dominio, incluidos sus teléfonos y
	 * estudios.
	 *
	 * @param personaEntity entidad leída de MariaDB
	 * @return persona del dominio completa
	 */
	public Person fromAdapterToDomain(PersonaEntity personaEntity) {
		Person person = toDomainBasic(personaEntity);
		person.setStudies(validateStudies(personaEntity.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaEntity.getTelefonos()));
		return person;
	}

	/**
	 * Convierte una entidad en persona del dominio sin teléfonos ni estudios. La
	 * usan los mappers de teléfono y estudio.
	 *
	 * @param personaEntity entidad leída de MariaDB
	 * @return persona con las listas vacías
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

	/**
	 * Convierte el valor de la columna al género del dominio.
	 *
	 * @param genero valor de la columna; puede ser nulo
	 * @return {@link Gender#FEMALE}, {@link Gender#MALE} u {@link Gender#OTHER}
	 */
	private static Gender validateGender(Character genero) {
		// equals evita el NullPointerException de comparar con == un Character nulo
		return Character.valueOf('F').equals(genero) ? Gender.FEMALE
				: Character.valueOf('M').equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	/**
	 * Normaliza la edad leída de la base de datos.
	 *
	 * @param edad valor de la columna
	 * @return la edad, o {@code null} si es nula o negativa
	 */
	private static Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	/**
	 * Convierte los estudios de la persona.
	 *
	 * @param estudiosEntity estudios leídos de MariaDB
	 * @return estudios del dominio; lista vacía si no tiene
	 */
	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	/**
	 * Convierte los teléfonos de la persona.
	 *
	 * @param telefonoEntities teléfonos leídos de MariaDB
	 * @return teléfonos del dominio; lista vacía si no tiene
	 */
	private List<Phone> validatePhones(List<TelefonoEntity> telefonoEntities) {
		return telefonoEntities != null && !telefonoEntities.isEmpty() ? telefonoEntities.stream()
				.map(telefono -> telefonoMapperMaria.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}

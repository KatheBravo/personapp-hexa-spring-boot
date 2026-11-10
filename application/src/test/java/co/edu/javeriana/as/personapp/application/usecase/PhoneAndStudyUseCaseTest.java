package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Pruebas unitarias de {@link PhoneUseCase} y {@link StudyUseCase}. Validan las
 * reglas que dependen de otras entidades: que existan el dueño del teléfono, la
 * persona y la profesión del estudio.
 */
class PhoneAndStudyUseCaseTest {

	/** Personas en memoria, compartidas por los casos de uso. */
	private InMemoryPorts.Persons persons;

	/** Profesiones en memoria. */
	private InMemoryPorts.Professions professions;

	/** Caso de uso de teléfonos bajo prueba. */
	private PhoneUseCase phoneUseCase;

	/** Caso de uso de estudios bajo prueba. */
	private StudyUseCase studyUseCase;

	/** Caso de uso de profesiones, usado para preparar los datos. */
	private ProfessionUseCase professionUseCase;

	/**
	 * Prepara en memoria una persona (cédula 123) y una profesión (identificador 1).
	 *
	 * @throws Exception si los datos de prueba no son válidos
	 */
	@BeforeEach
	void setUp() throws Exception {
		persons = new InMemoryPorts.Persons();
		professions = new InMemoryPorts.Professions();
		phoneUseCase = new PhoneUseCase(new InMemoryPorts.Phones(), persons);
		studyUseCase = new StudyUseCase(new InMemoryPorts.Studies(), persons, professions);
		professionUseCase = new ProfessionUseCase(professions);
		persons.save(new Person(123, "Pepe", "Perez", Gender.MALE));
		professionUseCase.create(new Profession(1, "Ingenieria de Sistemas"));
	}

	/**
	 * Crea un teléfono de prueba con operador Claro.
	 *
	 * @param number número
	 * @param ownerId cédula del dueño
	 * @return teléfono cuyo dueño solo tiene la cédula
	 */
	private static Phone phone(String number, int ownerId) {
		Person owner = new Person();
		owner.setIdentification(ownerId);
		return new Phone(number, "Claro", owner);
	}

	/**
	 * Crea un estudio de prueba en la universidad Javeriana.
	 *
	 * @param personId cédula de la persona
	 * @param professionId identificador de la profesión
	 * @param date fecha de grado; puede ser nula
	 * @return estudio con la persona y la profesión solo con su identificador
	 */
	private static Study study(int personId, int professionId, LocalDate date) {
		Person person = new Person();
		person.setIdentification(personId);
		Profession profession = new Profession();
		profession.setIdentification(professionId);
		Study study = new Study(person, profession);
		study.setGraduationDate(date);
		study.setUniversityName("Javeriana");
		return study;
	}

	/** Un teléfono de un dueño existente se crea con los datos completos del dueño. */
	@Test
	void createsAPhoneForAnExistingOwner() throws Exception {
		Phone created = phoneUseCase.create(phone("3001234567", 123));
		assertEquals("Pepe", created.getOwner().getFirstName());
		assertEquals(1, phoneUseCase.count());
	}

	/** No se crea un teléfono si el dueño no existe. */
	@Test
	void aPhoneNeedsAnExistingOwner() {
		assertThrows(NoExistException.class, () -> phoneUseCase.create(phone("3001234567", 999)));
	}

	/** Un número con letras o guiones se rechaza. */
	@Test
	void aPhoneNumberOnlyHasDigits() {
		assertThrows(InvalidDataException.class, () -> phoneUseCase.create(phone("300-ABC", 123)));
	}

	/** No se permite registrar dos veces el mismo número. */
	@Test
	void doesNotDuplicatePhones() throws Exception {
		phoneUseCase.create(phone("3001234567", 123));
		assertThrows(AlreadyExistException.class, () -> phoneUseCase.create(phone("3001234567", 123)));
	}

	/** Un estudio de una persona y una profesión existentes se crea con sus datos completos. */
	@Test
	void createsAStudyForExistingPersonAndProfession() throws Exception {
		Study created = studyUseCase.create(study(123, 1, LocalDate.of(2020, 6, 15)));
		assertEquals("Ingenieria de Sistemas", created.getProfession().getName());
		assertEquals(1, studyUseCase.count());
	}

	/** No se crea un estudio si la persona o la profesión no existen. */
	@Test
	void aStudyNeedsExistingPersonAndProfession() {
		assertThrows(NoExistException.class, () -> studyUseCase.create(study(999, 1, null)));
		assertThrows(NoExistException.class, () -> studyUseCase.create(study(123, 99, null)));
	}

	/** Una fecha de grado futura se rechaza. */
	@Test
	void aGraduationDateCannotBeInTheFuture() {
		assertThrows(InvalidDataException.class,
				() -> studyUseCase.create(study(123, 1, LocalDate.now().plusDays(1))));
	}

	/** Al editar un estudio cambian la fecha y la universidad, y la llave se conserva. */
	@Test
	void editingAStudyKeepsItsCompositeKey() throws Exception {
		studyUseCase.create(study(123, 1, LocalDate.of(2020, 6, 15)));
		Study edited = study(123, 1, LocalDate.of(2021, 1, 10));
		edited.setUniversityName("Universidad Nacional");
		studyUseCase.edit(123, 1, edited);
		assertEquals("Universidad Nacional", studyUseCase.findOne(123, 1).getUniversityName());
		assertEquals(1, studyUseCase.count());
	}
}

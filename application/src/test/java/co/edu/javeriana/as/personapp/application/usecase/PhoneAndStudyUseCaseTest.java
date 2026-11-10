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

class PhoneAndStudyUseCaseTest {

	private InMemoryPorts.Persons persons;
	private InMemoryPorts.Professions professions;
	private PhoneUseCase phoneUseCase;
	private StudyUseCase studyUseCase;
	private ProfessionUseCase professionUseCase;

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

	private static Phone phone(String number, int ownerId) {
		Person owner = new Person();
		owner.setIdentification(ownerId);
		return new Phone(number, "Claro", owner);
	}

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

	@Test
	void createsAPhoneForAnExistingOwner() throws Exception {
		Phone created = phoneUseCase.create(phone("3001234567", 123));
		assertEquals("Pepe", created.getOwner().getFirstName());
		assertEquals(1, phoneUseCase.count());
	}

	@Test
	void aPhoneNeedsAnExistingOwner() {
		assertThrows(NoExistException.class, () -> phoneUseCase.create(phone("3001234567", 999)));
	}

	@Test
	void aPhoneNumberOnlyHasDigits() {
		assertThrows(InvalidDataException.class, () -> phoneUseCase.create(phone("300-ABC", 123)));
	}

	@Test
	void doesNotDuplicatePhones() throws Exception {
		phoneUseCase.create(phone("3001234567", 123));
		assertThrows(AlreadyExistException.class, () -> phoneUseCase.create(phone("3001234567", 123)));
	}

	@Test
	void createsAStudyForExistingPersonAndProfession() throws Exception {
		Study created = studyUseCase.create(study(123, 1, LocalDate.of(2020, 6, 15)));
		assertEquals("Ingenieria de Sistemas", created.getProfession().getName());
		assertEquals(1, studyUseCase.count());
	}

	@Test
	void aStudyNeedsExistingPersonAndProfession() {
		assertThrows(NoExistException.class, () -> studyUseCase.create(study(999, 1, null)));
		assertThrows(NoExistException.class, () -> studyUseCase.create(study(123, 99, null)));
	}

	@Test
	void aGraduationDateCannotBeInTheFuture() {
		assertThrows(InvalidDataException.class,
				() -> studyUseCase.create(study(123, 1, LocalDate.now().plusDays(1))));
	}

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

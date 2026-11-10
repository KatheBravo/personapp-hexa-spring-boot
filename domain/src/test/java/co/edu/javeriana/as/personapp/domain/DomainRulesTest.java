package co.edu.javeriana.as.personapp.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DomainRulesTest {

	@Test
	void ageIsOptionalButNotNegative() {
		Person person = new Person(1, "Ana", "Gomez", Gender.FEMALE);
		assertTrue(person.isValidAge());
		person.setAge(0);
		assertTrue(person.isValidAge());
		person.setAge(-5);
		assertFalse(person.isValidAge());
	}

	@Test
	void namesAreRequiredAndHaveAMaximumLength() {
		assertTrue(new Person(1, "Ana", "Gomez", Gender.FEMALE).isValidName());
		assertFalse(new Person(1, " ", "Gomez", Gender.FEMALE).isValidName());
		assertFalse(new Person(1, "A".repeat(46), "Gomez", Gender.FEMALE).isValidName());
	}

	@Test
	void phoneNumbersOnlyHaveDigits() {
		Person owner = new Person(1, "Ana", "Gomez", Gender.FEMALE);
		assertTrue(new Phone("3001234567", "Claro", owner).isValidNumber());
		assertFalse(new Phone("300 123", "Claro", owner).isValidNumber());
		assertFalse(new Phone("123", "Claro", owner).isValidNumber());
	}

	@Test
	void graduationDateCannotBeInTheFuture() {
		Study study = new Study(new Person(1, "Ana", "Gomez", Gender.FEMALE), new Profession(1, "Medicina"));
		assertTrue(study.isValidGraduationDate());
		study.setGraduationDate(LocalDate.now().plusYears(1));
		assertFalse(study.isValidGraduationDate());
	}
}

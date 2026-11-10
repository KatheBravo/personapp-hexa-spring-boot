package co.edu.javeriana.as.personapp.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de validación de las entidades del dominio. No necesitan
 * Spring ni bases de datos.
 */
class DomainRulesTest {

	/** La edad es opcional y puede ser cero, pero no negativa. */
	@Test
	void ageIsOptionalButNotNegative() {
		Person person = new Person(1, "Ana", "Gomez", Gender.FEMALE);
		assertTrue(person.isValidAge());
		person.setAge(0);
		assertTrue(person.isValidAge());
		person.setAge(-5);
		assertFalse(person.isValidAge());
	}

	/** Nombre y apellido son obligatorios y tienen máximo 45 caracteres. */
	@Test
	void namesAreRequiredAndHaveAMaximumLength() {
		assertTrue(new Person(1, "Ana", "Gomez", Gender.FEMALE).isValidName());
		assertFalse(new Person(1, " ", "Gomez", Gender.FEMALE).isValidName());
		assertFalse(new Person(1, "A".repeat(46), "Gomez", Gender.FEMALE).isValidName());
	}

	/** El número de teléfono solo admite entre 7 y 15 dígitos. */
	@Test
	void phoneNumbersOnlyHaveDigits() {
		Person owner = new Person(1, "Ana", "Gomez", Gender.FEMALE);
		assertTrue(new Phone("3001234567", "Claro", owner).isValidNumber());
		assertFalse(new Phone("300 123", "Claro", owner).isValidNumber());
		assertFalse(new Phone("123", "Claro", owner).isValidNumber());
	}

	/** La fecha de grado no puede estar en el futuro. */
	@Test
	void graduationDateCannotBeInTheFuture() {
		Study study = new Study(new Person(1, "Ana", "Gomez", Gender.FEMALE), new Profession(1, "Medicina"));
		assertTrue(study.isValidGraduationDate());
		study.setGraduationDate(LocalDate.now().plusYears(1));
		assertFalse(study.isValidGraduationDate());
	}
}

package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;

class PersonUseCaseTest {

	private InMemoryPorts.Persons persons;
	private PersonUseCase useCase;

	@BeforeEach
	void setUp() {
		persons = new InMemoryPorts.Persons();
		useCase = new PersonUseCase(persons);
	}

	private static Person person(int cc, String name, Integer age) {
		Person person = new Person(cc, name, "Perez", Gender.MALE);
		person.setAge(age);
		return person;
	}

	@Test
	void createsAndFindsAPerson() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertEquals("Pepe", useCase.findOne(123).getFirstName());
		assertEquals(1, useCase.count());
	}

	@Test
	void doesNotCreateTheSamePersonTwice() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertThrows(AlreadyExistException.class, () -> useCase.create(person(123, "Otro", 20)));
	}

	@Test
	void rejectsNegativeAge() {
		assertThrows(InvalidDataException.class, () -> useCase.create(person(123, "Pepe", -1)));
	}

	@Test
	void editKeepsTheIdentificationOfThePath() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		useCase.edit(123, person(999, "Jose", 31));
		assertEquals("Jose", useCase.findOne(123).getFirstName());
		assertEquals(1, useCase.count());
	}

	@Test
	void editOrDropOfAMissingPersonFails() {
		assertThrows(NoExistException.class, () -> useCase.edit(1, person(1, "X", 1)));
		assertThrows(NoExistException.class, () -> useCase.drop(1));
	}

	@Test
	void dropsAPerson() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertTrue(useCase.drop(123));
		assertEquals(0, useCase.count());
	}

	@Test
	void theOutputPortCanBeChangedAtRuntime() throws Exception {
		InMemoryPorts.Persons otherDatabase = new InMemoryPorts.Persons();
		useCase.create(person(123, "Pepe", 30));
		useCase.setPersintence(otherDatabase);
		assertEquals(0, useCase.count());
		useCase.create(person(456, "Ana", 22));
		assertEquals(1, persons.data.size());
		assertEquals(1, otherDatabase.data.size());
	}
}

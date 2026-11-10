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

/**
 * Pruebas unitarias de {@link PersonUseCase} con un adaptador de salida en memoria.
 */
class PersonUseCaseTest {

	/** Adaptador de salida en memoria. */
	private InMemoryPorts.Persons persons;

	/** Caso de uso bajo prueba. */
	private PersonUseCase useCase;

	/** Crea un caso de uso nuevo con una base de datos en memoria vacía. */
	@BeforeEach
	void setUp() {
		persons = new InMemoryPorts.Persons();
		useCase = new PersonUseCase(persons);
	}

	/**
	 * Crea una persona de prueba.
	 *
	 * @param cc cédula
	 * @param name nombre
	 * @param age edad; puede ser nula
	 * @return persona con apellido Perez y género masculino
	 */
	private static Person person(int cc, String name, Integer age) {
		Person person = new Person(cc, name, "Perez", Gender.MALE);
		person.setAge(age);
		return person;
	}

	/** Una persona creada se puede consultar y se cuenta. */
	@Test
	void createsAndFindsAPerson() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertEquals("Pepe", useCase.findOne(123).getFirstName());
		assertEquals(1, useCase.count());
	}

	/** No se permite crear dos personas con la misma cédula. */
	@Test
	void doesNotCreateTheSamePersonTwice() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertThrows(AlreadyExistException.class, () -> useCase.create(person(123, "Otro", 20)));
	}

	/** Una edad negativa se rechaza antes de guardar. */
	@Test
	void rejectsNegativeAge() {
		assertThrows(InvalidDataException.class, () -> useCase.create(person(123, "Pepe", -1)));
	}

	/** Al editar se conserva la cédula recibida, aunque los datos traigan otra. */
	@Test
	void editKeepsTheIdentificationOfThePath() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		useCase.edit(123, person(999, "Jose", 31));
		assertEquals("Jose", useCase.findOne(123).getFirstName());
		assertEquals(1, useCase.count());
	}

	/** Editar o eliminar una persona inexistente lanza {@link NoExistException}. */
	@Test
	void editOrDropOfAMissingPersonFails() {
		assertThrows(NoExistException.class, () -> useCase.edit(1, person(1, "X", 1)));
		assertThrows(NoExistException.class, () -> useCase.drop(1));
	}

	/** Una persona eliminada deja de contarse. */
	@Test
	void dropsAPerson() throws Exception {
		useCase.create(person(123, "Pepe", 30));
		assertTrue(useCase.drop(123));
		assertEquals(0, useCase.count());
	}

	/** Al cambiar el puerto de salida, las operaciones siguientes usan la otra base de datos. */
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

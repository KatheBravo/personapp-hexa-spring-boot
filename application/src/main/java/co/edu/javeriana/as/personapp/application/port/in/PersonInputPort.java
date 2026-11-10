package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Puerto de entrada para la gestión de personas.
 *
 * Lo usan los adaptadores de entrada (REST y consola) y lo implementa
 * {@link co.edu.javeriana.as.personapp.application.usecase.PersonUseCase}.
 */
@Port
public interface PersonInputPort {

	/**
	 * Cambia el adaptador de salida que usan las operaciones siguientes. Así se
	 * elige el motor de persistencia en tiempo de ejecución.
	 *
	 * @param personPersintence adaptador de salida de personas del motor elegido
	 */
	public void setPersintence(PersonOutputPort personPersintence);

	/**
	 * Registra una persona nueva.
	 *
	 * @param person datos de la persona
	 * @return la persona guardada
	 * @throws AlreadyExistException si ya existe una persona con la misma cédula
	 * @throws InvalidDataException si los datos no cumplen las reglas del dominio
	 */
	public Person create(Person person) throws AlreadyExistException, InvalidDataException;

	/**
	 * Actualiza los datos de una persona. La cédula no cambia: se usa la que llega
	 * como parámetro.
	 *
	 * @param identification cédula de la persona a editar
	 * @param person datos nuevos
	 * @return la persona actualizada
	 * @throws NoExistException si no existe una persona con esa cédula
	 * @throws InvalidDataException si los datos nuevos no cumplen las reglas del dominio
	 */
	public Person edit(Integer identification, Person person) throws NoExistException, InvalidDataException;

	/**
	 * Elimina una persona junto con sus teléfonos y estudios.
	 *
	 * @param identification cédula de la persona
	 * @return {@code true} si la persona quedó eliminada
	 * @throws NoExistException si no existe una persona con esa cédula
	 */
	public Boolean drop(Integer identification) throws NoExistException;

	/**
	 * Consulta todas las personas registradas.
	 *
	 * @return lista de personas; vacía si no hay registros
	 */
	public List<Person> findAll();

	/**
	 * Busca una persona por su cédula.
	 *
	 * @param identification cédula de la persona
	 * @return la persona encontrada
	 * @throws NoExistException si no existe una persona con esa cédula
	 */
	public Person findOne(Integer identification) throws NoExistException;

	/**
	 * Cuenta las personas registradas.
	 *
	 * @return número de personas
	 */
	public Integer count();

	/**
	 * Consulta los teléfonos de una persona.
	 *
	 * @param identification cédula de la persona
	 * @return teléfonos de la persona
	 * @throws NoExistException si no existe una persona con esa cédula
	 */
	public List<Phone> getPhones(Integer identification) throws NoExistException;

	/**
	 * Consulta los estudios de una persona.
	 *
	 * @param identification cédula de la persona
	 * @return estudios de la persona
	 * @throws NoExistException si no existe una persona con esa cédula
	 */
	public List<Study> getStudies(Integer identification) throws NoExistException;
}

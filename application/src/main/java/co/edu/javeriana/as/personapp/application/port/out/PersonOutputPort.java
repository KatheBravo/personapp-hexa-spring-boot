package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Person;

/**
 * Puerto de salida para la persistencia de personas. Cada motor (MariaDB y
 * MongoDB) tiene un adaptador que lo implementa.
 */
@Port
public interface PersonOutputPort {
	/**
	 * Guarda una persona. Si ya existe, actualiza sus datos.
	 *
	 * @param person registro a guardar
	 * @return el registro tal como quedó guardado
	 */
	public Person save(Person person);

	/**
	 * Elimina una persona y sus teléfonos y estudios.
	 *
	 * @param identification cédula de la persona
	 * @return {@code true} si el registro ya no existe después de la operación
	 */
	public Boolean delete(Integer identification);

	/**
	 * Consulta todos los registros.
	 *
	 * @return lista de personas; vacía si no hay registros
	 */
	public List<Person> find();

	/**
	 * Busca una persona por su cédula.
	 *
	 * @param identification cédula de la persona
	 * @return el registro encontrado, o {@code null} si no existe
	 */
	public Person findById(Integer identification);
}

package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;

/**
 * Puerto de entrada para la gestión de teléfonos.
 */
@Port
public interface PhoneInputPort {

	/**
	 * Cambia los adaptadores de salida que usan las operaciones siguientes. Ambos
	 * deben ser del mismo motor, porque el dueño del teléfono se valida en la misma
	 * base de datos.
	 *
	 * @param phonePersistence adaptador de salida de teléfonos
	 * @param personPersistence adaptador de salida de personas, usado para validar el dueño
	 */
	public void setPersistence(PhoneOutputPort phonePersistence, PersonOutputPort personPersistence);

	/**
	 * Registra un teléfono para una persona existente.
	 *
	 * @param phone datos del teléfono; del dueño basta con la cédula
	 * @return el teléfono guardado, con los datos completos del dueño
	 * @throws AlreadyExistException si el número ya está registrado
	 * @throws NoExistException si el dueño no existe
	 * @throws InvalidDataException si los datos no cumplen las reglas del dominio
	 */
	public Phone create(Phone phone) throws AlreadyExistException, NoExistException, InvalidDataException;

	/**
	 * Actualiza el operador o el dueño de un teléfono. El número no cambia.
	 *
	 * @param number número del teléfono a editar
	 * @param phone datos nuevos
	 * @return el teléfono actualizado
	 * @throws NoExistException si el teléfono o el nuevo dueño no existen
	 * @throws InvalidDataException si los datos nuevos no cumplen las reglas del dominio
	 */
	public Phone edit(String number, Phone phone) throws NoExistException, InvalidDataException;

	/**
	 * Elimina un teléfono.
	 *
	 * @param number número del teléfono
	 * @return {@code true} si el teléfono quedó eliminado
	 * @throws NoExistException si el teléfono no existe
	 */
	public Boolean drop(String number) throws NoExistException;

	/**
	 * Consulta todos los teléfonos registrados.
	 *
	 * @return lista de teléfonos; vacía si no hay registros
	 */
	public List<Phone> findAll();

	/**
	 * Busca un teléfono por su número.
	 *
	 * @param number número del teléfono
	 * @return el teléfono encontrado
	 * @throws NoExistException si el teléfono no existe
	 */
	public Phone findOne(String number) throws NoExistException;

	/**
	 * Cuenta los teléfonos registrados.
	 *
	 * @return número de teléfonos
	 */
	public Integer count();
}

package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Phone;

/**
 * Puerto de salida para la persistencia de teléfonos. Cada motor (MariaDB y
 * MongoDB) tiene un adaptador que lo implementa.
 */
@Port
public interface PhoneOutputPort {
	/**
	 * Guarda un teléfono. Si ya existe, actualiza sus datos.
	 *
	 * @param phone registro a guardar
	 * @return el registro tal como quedó guardado
	 */
	public Phone save(Phone phone);

	/**
	 * Elimina un teléfono.
	 *
	 * @param number número del teléfono
	 * @return {@code true} si el registro ya no existe después de la operación
	 */
	public Boolean delete(String number);

	/**
	 * Consulta todos los registros.
	 *
	 * @return lista de teléfonos; vacía si no hay registros
	 */
	public List<Phone> find();

	/**
	 * Busca un teléfono por su número.
	 *
	 * @param number número del teléfono
	 * @return el registro encontrado, o {@code null} si no existe
	 */
	public Phone findById(String number);
}

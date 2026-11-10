package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Profession;

/**
 * Puerto de salida para la persistencia de profesiones. Cada motor (MariaDB y
 * MongoDB) tiene un adaptador que lo implementa.
 */
@Port
public interface ProfessionOutputPort {
	/**
	 * Guarda una profesión. Si ya existe, actualiza sus datos.
	 *
	 * @param profession registro a guardar
	 * @return el registro tal como quedó guardado
	 */
	public Profession save(Profession profession);

	/**
	 * Elimina una profesión y los estudios asociados.
	 *
	 * @param identification identificador de la profesión
	 * @return {@code true} si el registro ya no existe después de la operación
	 */
	public Boolean delete(Integer identification);

	/**
	 * Consulta todos los registros.
	 *
	 * @return lista de profesiones; vacía si no hay registros
	 */
	public List<Profession> find();

	/**
	 * Busca una profesión por su identificador.
	 *
	 * @param identification identificador de la profesión
	 * @return el registro encontrado, o {@code null} si no existe
	 */
	public Profession findById(Integer identification);
}

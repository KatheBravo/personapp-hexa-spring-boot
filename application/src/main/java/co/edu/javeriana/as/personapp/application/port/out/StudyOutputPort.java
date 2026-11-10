package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Puerto de salida para la persistencia de estudios. Cada motor (MariaDB y
 * MongoDB) tiene un adaptador que lo implementa.
 *
 * Un estudio se identifica por la pareja cédula de la persona e identificador de
 * la profesión, igual que la llave primaria compuesta de la tabla {@code estudios}.
 */
@Port
public interface StudyOutputPort {
	/**
	 * Guarda un estudio. Si ya existe, actualiza sus datos.
	 *
	 * @param study registro a guardar
	 * @return el registro tal como quedó guardado
	 */
	public Study save(Study study);

	/**
	 * Elimina un estudio.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return {@code true} si el registro ya no existe después de la operación
	 */
	public Boolean delete(Integer personIdentification, Integer professionIdentification);

	/**
	 * Consulta todos los registros.
	 *
	 * @return lista de estudios; vacía si no hay registros
	 */
	public List<Study> find();

	/**
	 * Busca un estudio por su llave compuesta.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return el registro encontrado, o {@code null} si no existe
	 */
	public Study findById(Integer personIdentification, Integer professionIdentification);
}

package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Puerto de entrada para la gestión de estudios. Un estudio se identifica por la
 * cédula de la persona y el identificador de la profesión.
 */
@Port
public interface StudyInputPort {

	/**
	 * Cambia los adaptadores de salida que usan las operaciones siguientes. Los tres
	 * deben ser del mismo motor, porque la persona y la profesión del estudio se
	 * validan en la misma base de datos.
	 *
	 * @param studyPersistence adaptador de salida de estudios
	 * @param personPersistence adaptador de salida de personas
	 * @param professionPersistence adaptador de salida de profesiones
	 */
	public void setPersistence(StudyOutputPort studyPersistence, PersonOutputPort personPersistence,
			ProfessionOutputPort professionPersistence);

	/**
	 * Registra el estudio de una persona en una profesión.
	 *
	 * @param study datos del estudio; de la persona y la profesión basta con sus
	 *            identificadores
	 * @return el estudio guardado, con los datos completos de la persona y la profesión
	 * @throws AlreadyExistException si la persona ya tiene registrado ese estudio
	 * @throws NoExistException si la persona o la profesión no existen
	 * @throws InvalidDataException si los datos no cumplen las reglas del dominio
	 */
	public Study create(Study study) throws AlreadyExistException, NoExistException, InvalidDataException;

	/**
	 * Actualiza la fecha de grado y la universidad de un estudio. La persona y la
	 * profesión no cambian.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @param study datos nuevos
	 * @return el estudio actualizado
	 * @throws NoExistException si el estudio no existe
	 * @throws InvalidDataException si los datos nuevos no cumplen las reglas del dominio
	 */
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException, InvalidDataException;

	/**
	 * Elimina un estudio.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return {@code true} si el estudio quedó eliminado
	 * @throws NoExistException si el estudio no existe
	 */
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	/**
	 * Consulta todos los estudios registrados.
	 *
	 * @return lista de estudios; vacía si no hay registros
	 */
	public List<Study> findAll();

	/**
	 * Busca un estudio por su llave compuesta.
	 *
	 * @param personIdentification cédula de la persona
	 * @param professionIdentification identificador de la profesión
	 * @return el estudio encontrado
	 * @throws NoExistException si el estudio no existe
	 */
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	/**
	 * Cuenta los estudios registrados.
	 *
	 * @return número de estudios
	 */
	public Integer count();
}

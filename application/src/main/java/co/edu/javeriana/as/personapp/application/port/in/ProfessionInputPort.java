package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Puerto de entrada para la gestión de profesiones.
 */
@Port
public interface ProfessionInputPort {

	/**
	 * Cambia el adaptador de salida que usan las operaciones siguientes.
	 *
	 * @param professionPersistence adaptador de salida de profesiones del motor elegido
	 */
	public void setPersistence(ProfessionOutputPort professionPersistence);

	/**
	 * Registra una profesión nueva.
	 *
	 * @param profession datos de la profesión
	 * @return la profesión guardada
	 * @throws AlreadyExistException si ya existe una profesión con el mismo identificador
	 * @throws InvalidDataException si los datos no cumplen las reglas del dominio
	 */
	public Profession create(Profession profession) throws AlreadyExistException, InvalidDataException;

	/**
	 * Actualiza el nombre y la descripción de una profesión. El identificador no
	 * cambia.
	 *
	 * @param identification identificador de la profesión a editar
	 * @param profession datos nuevos
	 * @return la profesión actualizada
	 * @throws NoExistException si la profesión no existe
	 * @throws InvalidDataException si los datos nuevos no cumplen las reglas del dominio
	 */
	public Profession edit(Integer identification, Profession profession) throws NoExistException, InvalidDataException;

	/**
	 * Elimina una profesión junto con los estudios asociados.
	 *
	 * @param identification identificador de la profesión
	 * @return {@code true} si la profesión quedó eliminada
	 * @throws NoExistException si la profesión no existe
	 */
	public Boolean drop(Integer identification) throws NoExistException;

	/**
	 * Consulta todas las profesiones registradas.
	 *
	 * @return lista de profesiones; vacía si no hay registros
	 */
	public List<Profession> findAll();

	/**
	 * Busca una profesión por su identificador.
	 *
	 * @param identification identificador de la profesión
	 * @return la profesión encontrada
	 * @throws NoExistException si la profesión no existe
	 */
	public Profession findOne(Integer identification) throws NoExistException;

	/**
	 * Cuenta las profesiones registradas.
	 *
	 * @return número de profesiones
	 */
	public Integer count();

	/**
	 * Consulta los estudios registrados en una profesión.
	 *
	 * @param identification identificador de la profesión
	 * @return estudios de la profesión
	 * @throws NoExistException si la profesión no existe
	 */
	public List<Study> getStudies(Integer identification) throws NoExistException;
}

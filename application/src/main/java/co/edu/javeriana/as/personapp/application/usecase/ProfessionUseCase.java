package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de uso de profesiones: valida el identificador y el nombre antes de
 * delegar en el puerto de salida.
 */
@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

	/** Adaptador de salida del motor de persistencia en uso. */
	private ProfessionOutputPort professionPersistence;

	/**
	 * Crea el caso de uso con el adaptador de salida indicado.
	 *
	 * @param professionPersistence adaptador de salida de profesiones; por defecto el de MariaDB
	 */
	public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersistence) {
		this.professionPersistence = professionPersistence;
	}

	/**
	 * Cambia el adaptador de salida de profesiones.
	 *
	 * @param professionPersistence adaptador de salida del motor elegido
	 */
	@Override
	public void setPersistence(ProfessionOutputPort professionPersistence) {
		this.professionPersistence = professionPersistence;
	}

	/**
	 * Valida los datos y registra la profesión si su identificador no existe.
	 *
	 * @param profession datos de la profesión
	 * @return la profesión guardada
	 * @throws AlreadyExistException si el identificador ya está registrado
	 * @throws InvalidDataException si los datos no son válidos
	 */
	@Override
	public Profession create(Profession profession) throws AlreadyExistException, InvalidDataException {
		log.debug("Into create profession on Application Domain");
		validate(profession);
		if (professionPersistence.findById(profession.getIdentification()) != null) {
			throw new AlreadyExistException("The profession with id " + profession.getIdentification()
					+ " already exists into db, cannot be created");
		}
		return professionPersistence.save(profession);
	}

	/**
	 * Actualiza una profesión existente conservando el identificador recibido.
	 *
	 * @param identification identificador de la profesión
	 * @param profession datos nuevos
	 * @return la profesión actualizada
	 * @throws NoExistException si la profesión no existe
	 * @throws InvalidDataException si los datos nuevos no son válidos
	 */
	@Override
	public Profession edit(Integer identification, Profession profession)
			throws NoExistException, InvalidDataException {
		if (professionPersistence.findById(identification) == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be edited");
		}
		profession.setIdentification(identification);
		validate(profession);
		return professionPersistence.save(profession);
	}

	/**
	 * Elimina la profesión si existe.
	 *
	 * @param identification identificador de la profesión
	 * @return {@code true} si quedó eliminada
	 * @throws NoExistException si la profesión no existe
	 */
	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		if (professionPersistence.findById(identification) == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be dropped");
		}
		return professionPersistence.delete(identification);
	}

	/**
	 * Consulta todas las profesiones del motor configurado.
	 *
	 * @return lista de profesiones
	 */
	@Override
	public List<Profession> findAll() {
		log.info("Output: " + professionPersistence.getClass());
		return professionPersistence.find();
	}

	/**
	 * Busca una profesión por identificador.
	 *
	 * @param identification identificador de la profesión
	 * @return la profesión encontrada
	 * @throws NoExistException si la profesión no existe
	 */
	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession profession = professionPersistence.findById(identification);
		if (profession == null) {
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot be found");
		}
		return profession;
	}

	/**
	 * Cuenta las profesiones del motor configurado.
	 *
	 * @return número de profesiones
	 */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/**
	 * Consulta los estudios registrados en una profesión.
	 *
	 * @param identification identificador de la profesión
	 * @return estudios de la profesión
	 * @throws NoExistException si la profesión no existe
	 */
	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		return findOne(identification).getStudies();
	}

	/**
	 * Aplica las reglas de negocio de una profesión: identificador positivo y nombre
	 * obligatorio de máximo 90 caracteres.
	 *
	 * @param profession profesión a validar
	 * @throws InvalidDataException con el motivo de la primera regla que no se cumple
	 */
	private void validate(Profession profession) throws InvalidDataException {
		if (profession == null) {
			throw new InvalidDataException("The profession is required");
		}
		if (!profession.isValidIdentification()) {
			throw new InvalidDataException("The profession id must be a positive number");
		}
		if (!profession.isValidName()) {
			throw new InvalidDataException("The profession name is required (max 90 characters)");
		}
	}
}

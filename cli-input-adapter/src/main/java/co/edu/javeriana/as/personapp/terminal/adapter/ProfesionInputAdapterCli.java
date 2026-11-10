package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada por consola para profesiones. Crea el caso de uso con el
 * adaptador de salida del motor que el usuario elige en el menú.
 */
@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

	/** Adaptador de salida de profesiones en MariaDB. */
	private final ProfessionOutputPort professionOutputPortMaria;

	/** Adaptador de salida de profesiones en MongoDB. */
	private final ProfessionOutputPort professionOutputPortMongo;

	/** Conversor del modelo de consola de profesión. */
	private final ProfesionMapperCli profesionMapperCli;

	/** Conversor del modelo de consola de estudio. */
	private final EstudioMapperCli estudioMapperCli;

	/** Caso de uso conectado al motor elegido en el menú. */
	private ProfessionInputPort professionInputPort;

	/** Motor elegido en el menú. */
	private DatabaseOption motor;

	/**
	 * Recibe los adaptadores de salida de ambos motores y los conversores de consola.
	 *
	 * @param professionOutputPortMaria profesiones en MariaDB
	 * @param professionOutputPortMongo profesiones en MongoDB
	 * @param profesionMapperCli conversor de profesiones
	 * @param estudioMapperCli conversor de estudios
	 */
	public ProfesionInputAdapterCli(
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			ProfesionMapperCli profesionMapperCli, EstudioMapperCli estudioMapperCli) {
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.profesionMapperCli = profesionMapperCli;
		this.estudioMapperCli = estudioMapperCli;
	}

	/**
	 * Conecta el adaptador al motor elegido creando el caso de uso con sus puertos de
	 * salida. Las operaciones siguientes usan ese motor hasta que se elija otro.
	 *
	 * @param dbOption motor elegido: MARIA o MONGO
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		professionInputPort = motor == DatabaseOption.MARIA ? new ProfessionUseCase(professionOutputPortMaria)
				: new ProfessionUseCase(professionOutputPortMongo);
	}

	/**
	 * Devuelve el nombre del motor en uso.
	 *
	 * @return nombre del motor, o {@code -} si todavía no se eligió
	 */
	public String motorActual() {
		return motor != null ? motor.getLabel() : "-";
	}

	/**
	 * Imprime las profesiones del motor en uso.
	 */
	public void historial() {
		log.info("Into historial ProfesionEntity in Input Adapter");
		List<ProfesionModelCli> profesiones = professionInputPort.findAll().stream()
				.map(profesionMapperCli::fromDomainToAdapterCli).toList();
		if (profesiones.isEmpty()) {
			System.out.println("No hay profesiones registradas en " + motorActual());
		}
		profesiones.forEach(System.out::println);
	}

	/**
	 * Busca una profesión e imprime el resultado o el motivo por el que no existe.
	 *
	 * @param id identificador de la profesión
	 */
	public void buscar(Integer id) {
		try {
			System.out.println(profesionMapperCli.fromDomainToAdapterCli(professionInputPort.findOne(id)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Crea una profesión e imprime el resultado o el motivo por el que no se pudo crear.
	 *
	 * @param profesion datos leídos de la consola
	 */
	public void crear(ProfesionModelCli profesion) {
		try {
			System.out.println("Creada: " + profesionMapperCli.fromDomainToAdapterCli(
					professionInputPort.create(profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (AlreadyExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	/**
	 * Edita una profesión e imprime el resultado.
	 *
	 * @param profesion datos nuevos, con el identificador de la profesión a editar
	 */
	public void editar(ProfesionModelCli profesion) {
		try {
			System.out.println("Editada: " + profesionMapperCli.fromDomainToAdapterCli(professionInputPort
					.edit(profesion.getId(), profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	/**
	 * Elimina una profesión con sus estudios e imprime el resultado.
	 *
	 * @param id identificador de la profesión
	 */
	public void eliminar(Integer id) {
		try {
			boolean eliminada = Boolean.TRUE.equals(professionInputPort.drop(id));
			System.out.println(eliminada ? "Profesion " + id + " eliminada (con sus estudios)"
					: "La profesion " + id + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Imprime el número de profesiones del motor en uso.
	 */
	public void contar() {
		System.out.println("Total de profesiones en " + motorActual() + ": " + professionInputPort.count());
	}

	/**
	 * Imprime los estudios registrados en una profesión.
	 *
	 * @param id identificador de la profesión
	 */
	public void estudios(Integer id) {
		try {
			List<Study> studies = professionInputPort.getStudies(id);
			if (studies.isEmpty()) {
				System.out.println("Nadie tiene registrada la profesion " + id);
			}
			studies.stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}
}

package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada por consola para estudios.
 *
 * Recibe las dos implementaciones de cada puerto de salida. Cuando el usuario
 * elige el motor en el menú se crea el caso de uso con las de ese motor; los
 * puertos de estudios, personas y profesiones siempre son del mismo motor.
 */
@Slf4j
@Adapter
public class EstudioInputAdapterCli {

	/** Adaptador de salida de estudios en MariaDB. */
	private final StudyOutputPort studyOutputPortMaria;

	/** Adaptador de salida de estudios en MongoDB. */
	private final StudyOutputPort studyOutputPortMongo;

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Adaptador de salida de profesiones en MariaDB. */
	private final ProfessionOutputPort professionOutputPortMaria;

	/** Adaptador de salida de profesiones en MongoDB. */
	private final ProfessionOutputPort professionOutputPortMongo;

	/** Conversor entre el dominio y el modelo de consola de estudio. */
	private final EstudioMapperCli estudioMapperCli;

	/** Caso de uso conectado al motor elegido en el menú. */
	private StudyInputPort studyInputPort;

	/** Motor elegido en el menú. */
	private DatabaseOption motor;

	/**
	 * Recibe los adaptadores de salida de ambos motores. Spring los distingue por el
	 * nombre del bean indicado en {@code @Qualifier}.
	 *
	 * @param studyOutputPortMaria estudios en MariaDB
	 * @param studyOutputPortMongo estudios en MongoDB
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param professionOutputPortMaria profesiones en MariaDB
	 * @param professionOutputPortMongo profesiones en MongoDB
	 * @param estudioMapperCli conversor del modelo de consola
	 */
	public EstudioInputAdapterCli(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyOutputPortMaria,
			@Qualifier("studyOutputAdapterMongo") StudyOutputPort studyOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			EstudioMapperCli estudioMapperCli) {
		this.studyOutputPortMaria = studyOutputPortMaria;
		this.studyOutputPortMongo = studyOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.estudioMapperCli = estudioMapperCli;
	}

	/**
	 * Conecta el adaptador al motor elegido creando el caso de uso con sus puertos de
	 * salida. Las operaciones siguientes usan ese motor hasta que se elija otro.
	 *
	 * @param dbOption motor elegido: MARIA o MONGO
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		studyInputPort = motor == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
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
	 * Imprime los estudios del motor en uso.
	 */
	public void historial() {
		log.info("Into historial EstudiosEntity in Input Adapter");
		List<EstudioModelCli> estudios = studyInputPort.findAll().stream()
				.map(estudioMapperCli::fromDomainToAdapterCli).toList();
		if (estudios.isEmpty()) {
			System.out.println("No hay estudios registrados en " + motorActual());
		}
		estudios.forEach(System.out::println);
	}

	/**
	 * Busca un estudio e imprime el resultado o el motivo por el que no existe.
	 *
	 * @param ccPersona cédula de la persona
	 * @param idProfesion identificador de la profesión
	 */
	public void buscar(Integer ccPersona, Integer idProfesion) {
		try {
			System.out.println(estudioMapperCli.fromDomainToAdapterCli(studyInputPort.findOne(ccPersona, idProfesion)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Crea un estudio e imprime el resultado o el motivo por el que no se pudo crear.
	 *
	 * @param estudio datos leídos de la consola
	 */
	public void crear(EstudioModelCli estudio) {
		try {
			System.out.println("Creado: " + estudioMapperCli
					.fromDomainToAdapterCli(studyInputPort.create(estudioMapperCli.fromAdapterCliToDomain(estudio))));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	/**
	 * Edita la fecha de grado y la universidad de un estudio e imprime el resultado.
	 *
	 * @param estudio datos leídos de la consola, con la llave del estudio
	 */
	public void editar(EstudioModelCli estudio) {
		try {
			System.out.println("Editado: " + estudioMapperCli.fromDomainToAdapterCli(studyInputPort.edit(
					estudio.getCcPersona(), estudio.getIdProfesion(), estudioMapperCli.fromAdapterCliToDomain(estudio))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	/**
	 * Elimina un estudio e imprime el resultado.
	 *
	 * @param ccPersona cédula de la persona
	 * @param idProfesion identificador de la profesión
	 */
	public void eliminar(Integer ccPersona, Integer idProfesion) {
		try {
			boolean eliminado = Boolean.TRUE.equals(studyInputPort.drop(ccPersona, idProfesion));
			System.out.println(eliminado ? "Estudio eliminado" : "El estudio no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Imprime el número de estudios del motor en uso.
	 */
	public void contar() {
		System.out.println("Total de estudios en " + motorActual() + ": " + studyInputPort.count());
	}
}

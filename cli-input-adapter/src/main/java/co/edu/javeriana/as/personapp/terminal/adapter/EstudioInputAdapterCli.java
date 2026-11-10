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

@Slf4j
@Adapter
public class EstudioInputAdapterCli {

	private final StudyOutputPort studyOutputPortMaria;
	private final StudyOutputPort studyOutputPortMongo;
	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final ProfessionOutputPort professionOutputPortMaria;
	private final ProfessionOutputPort professionOutputPortMongo;
	private final EstudioMapperCli estudioMapperCli;

	private StudyInputPort studyInputPort;
	private DatabaseOption motor;

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

	public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		studyInputPort = motor == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
	}

	public String motorActual() {
		return motor != null ? motor.getLabel() : "-";
	}

	public void historial() {
		log.info("Into historial EstudiosEntity in Input Adapter");
		List<EstudioModelCli> estudios = studyInputPort.findAll().stream()
				.map(estudioMapperCli::fromDomainToAdapterCli).toList();
		if (estudios.isEmpty()) {
			System.out.println("No hay estudios registrados en " + motorActual());
		}
		estudios.forEach(System.out::println);
	}

	public void buscar(Integer ccPersona, Integer idProfesion) {
		try {
			System.out.println(estudioMapperCli.fromDomainToAdapterCli(studyInputPort.findOne(ccPersona, idProfesion)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void crear(EstudioModelCli estudio) {
		try {
			System.out.println("Creado: " + estudioMapperCli
					.fromDomainToAdapterCli(studyInputPort.create(estudioMapperCli.fromAdapterCliToDomain(estudio))));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	public void editar(EstudioModelCli estudio) {
		try {
			System.out.println("Editado: " + estudioMapperCli.fromDomainToAdapterCli(studyInputPort.edit(
					estudio.getCcPersona(), estudio.getIdProfesion(), estudioMapperCli.fromAdapterCliToDomain(estudio))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	public void eliminar(Integer ccPersona, Integer idProfesion) {
		try {
			boolean eliminado = Boolean.TRUE.equals(studyInputPort.drop(ccPersona, idProfesion));
			System.out.println(eliminado ? "Estudio eliminado" : "El estudio no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de estudios en " + motorActual() + ": " + studyInputPort.count());
	}
}

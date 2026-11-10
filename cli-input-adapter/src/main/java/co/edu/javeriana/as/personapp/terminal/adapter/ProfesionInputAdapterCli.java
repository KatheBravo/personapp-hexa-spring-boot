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

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

	private final ProfessionOutputPort professionOutputPortMaria;
	private final ProfessionOutputPort professionOutputPortMongo;
	private final ProfesionMapperCli profesionMapperCli;
	private final EstudioMapperCli estudioMapperCli;

	private ProfessionInputPort professionInputPort;
	private DatabaseOption motor;

	public ProfesionInputAdapterCli(
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			ProfesionMapperCli profesionMapperCli, EstudioMapperCli estudioMapperCli) {
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.profesionMapperCli = profesionMapperCli;
		this.estudioMapperCli = estudioMapperCli;
	}

	public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		professionInputPort = motor == DatabaseOption.MARIA ? new ProfessionUseCase(professionOutputPortMaria)
				: new ProfessionUseCase(professionOutputPortMongo);
	}

	public String motorActual() {
		return motor != null ? motor.getLabel() : "-";
	}

	public void historial() {
		log.info("Into historial ProfesionEntity in Input Adapter");
		List<ProfesionModelCli> profesiones = professionInputPort.findAll().stream()
				.map(profesionMapperCli::fromDomainToAdapterCli).toList();
		if (profesiones.isEmpty()) {
			System.out.println("No hay profesiones registradas en " + motorActual());
		}
		profesiones.forEach(System.out::println);
	}

	public void buscar(Integer id) {
		try {
			System.out.println(profesionMapperCli.fromDomainToAdapterCli(professionInputPort.findOne(id)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void crear(ProfesionModelCli profesion) {
		try {
			System.out.println("Creada: " + profesionMapperCli.fromDomainToAdapterCli(
					professionInputPort.create(profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (AlreadyExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	public void editar(ProfesionModelCli profesion) {
		try {
			System.out.println("Editada: " + profesionMapperCli.fromDomainToAdapterCli(professionInputPort
					.edit(profesion.getId(), profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	public void eliminar(Integer id) {
		try {
			boolean eliminada = Boolean.TRUE.equals(professionInputPort.drop(id));
			System.out.println(eliminada ? "Profesion " + id + " eliminada (con sus estudios)"
					: "La profesion " + id + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de profesiones en " + motorActual() + ": " + professionInputPort.count());
	}

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

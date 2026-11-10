package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada por consola para personas. Recibe las dos
 * implementaciones del puerto de salida y arma el caso de uso con la que el
 * usuario elige en el menu (cambio de motor en caliente).
 */
@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final PersonaMapperCli personaMapperCli;
	private final TelefonoMapperCli telefonoMapperCli;
	private final EstudioMapperCli estudioMapperCli;

	private PersonInputPort personInputPort;
	private DatabaseOption motor;

	public PersonaInputAdapterCli(@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			PersonaMapperCli personaMapperCli, TelefonoMapperCli telefonoMapperCli,
			EstudioMapperCli estudioMapperCli) {
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.personaMapperCli = personaMapperCli;
		this.telefonoMapperCli = telefonoMapperCli;
		this.estudioMapperCli = estudioMapperCli;
	}

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		personInputPort = motor == DatabaseOption.MARIA ? new PersonUseCase(personOutputPortMaria)
				: new PersonUseCase(personOutputPortMongo);
	}

	public String motorActual() {
		return motor != null ? motor.getLabel() : "-";
	}

	public void historial() {
		log.info("Into historial PersonaEntity in Input Adapter");
		List<PersonaModelCli> personas = personInputPort.findAll().stream()
				.map(personaMapperCli::fromDomainToAdapterCli).toList();
		if (personas.isEmpty()) {
			System.out.println("No hay personas registradas en " + motorActual());
		}
		personas.forEach(System.out::println);
	}

	public void buscar(Integer cc) {
		try {
			System.out.println(personaMapperCli.fromDomainToAdapterCli(personInputPort.findOne(cc)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void crear(PersonaModelCli persona) {
		try {
			System.out.println("Creada: " + personaMapperCli
					.fromDomainToAdapterCli(personInputPort.create(personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (AlreadyExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	public void editar(PersonaModelCli persona) {
		try {
			System.out.println("Editada: " + personaMapperCli.fromDomainToAdapterCli(
					personInputPort.edit(persona.getCc(), personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	public void eliminar(Integer cc) {
		try {
			boolean eliminada = Boolean.TRUE.equals(personInputPort.drop(cc));
			System.out.println(eliminada ? "Persona " + cc + " eliminada (con sus telefonos y estudios)"
					: "La persona " + cc + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de personas en " + motorActual() + ": " + personInputPort.count());
	}

	public void telefonos(Integer cc) {
		try {
			List<Phone> phones = personInputPort.getPhones(cc);
			if (phones.isEmpty()) {
				System.out.println("La persona " + cc + " no tiene telefonos");
			}
			phones.stream().map(telefonoMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void estudios(Integer cc) {
		try {
			List<Study> studies = personInputPort.getStudies(cc);
			if (studies.isEmpty()) {
				System.out.println("La persona " + cc + " no tiene estudios");
			}
			studies.stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

}

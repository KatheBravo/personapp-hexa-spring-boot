package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

	private final PhoneOutputPort phoneOutputPortMaria;
	private final PhoneOutputPort phoneOutputPortMongo;
	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final TelefonoMapperCli telefonoMapperCli;

	private PhoneInputPort phoneInputPort;
	private DatabaseOption motor;

	public TelefonoInputAdapterCli(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phoneOutputPortMaria,
			@Qualifier("phoneOutputAdapterMongo") PhoneOutputPort phoneOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			TelefonoMapperCli telefonoMapperCli) {
		this.phoneOutputPortMaria = phoneOutputPortMaria;
		this.phoneOutputPortMongo = phoneOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.telefonoMapperCli = telefonoMapperCli;
	}

	public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		phoneInputPort = motor == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	public String motorActual() {
		return motor != null ? motor.getLabel() : "-";
	}

	public void historial() {
		log.info("Into historial TelefonoEntity in Input Adapter");
		List<TelefonoModelCli> telefonos = phoneInputPort.findAll().stream()
				.map(telefonoMapperCli::fromDomainToAdapterCli).toList();
		if (telefonos.isEmpty()) {
			System.out.println("No hay telefonos registrados en " + motorActual());
		}
		telefonos.forEach(System.out::println);
	}

	public void buscar(String numero) {
		try {
			System.out.println(telefonoMapperCli.fromDomainToAdapterCli(phoneInputPort.findOne(numero)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void crear(TelefonoModelCli telefono) {
		try {
			System.out.println("Creado: " + telefonoMapperCli
					.fromDomainToAdapterCli(phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	public void editar(TelefonoModelCli telefono) {
		try {
			System.out.println("Editado: " + telefonoMapperCli.fromDomainToAdapterCli(
					phoneInputPort.edit(telefono.getNumero(), telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	public void eliminar(String numero) {
		try {
			boolean eliminado = Boolean.TRUE.equals(phoneInputPort.drop(numero));
			System.out.println(eliminado ? "Telefono " + numero + " eliminado"
					: "El telefono " + numero + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de telefonos en " + motorActual() + ": " + phoneInputPort.count());
	}
}

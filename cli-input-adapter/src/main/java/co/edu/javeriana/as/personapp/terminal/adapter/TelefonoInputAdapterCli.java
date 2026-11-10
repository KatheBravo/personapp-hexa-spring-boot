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

/**
 * Adaptador de entrada por consola para teléfonos. Los puertos de teléfonos y de
 * personas siempre son del mismo motor, porque el dueño se valida en esa base de
 * datos.
 */
@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

	/** Adaptador de salida de teléfonos en MariaDB. */
	private final PhoneOutputPort phoneOutputPortMaria;

	/** Adaptador de salida de teléfonos en MongoDB. */
	private final PhoneOutputPort phoneOutputPortMongo;

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Conversor del modelo de consola de teléfono. */
	private final TelefonoMapperCli telefonoMapperCli;

	/** Caso de uso conectado al motor elegido en el menú. */
	private PhoneInputPort phoneInputPort;

	/** Motor elegido en el menú. */
	private DatabaseOption motor;

	/**
	 * Recibe los adaptadores de salida de ambos motores y el conversor de consola.
	 *
	 * @param phoneOutputPortMaria teléfonos en MariaDB
	 * @param phoneOutputPortMongo teléfonos en MongoDB
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param telefonoMapperCli conversor de teléfonos
	 */
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

	/**
	 * Conecta el adaptador al motor elegido creando el caso de uso con sus puertos de
	 * salida. Las operaciones siguientes usan ese motor hasta que se elija otro.
	 *
	 * @param dbOption motor elegido: MARIA o MONGO
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		phoneInputPort = motor == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
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
	 * Imprime los teléfonos del motor en uso.
	 */
	public void historial() {
		log.info("Into historial TelefonoEntity in Input Adapter");
		List<TelefonoModelCli> telefonos = phoneInputPort.findAll().stream()
				.map(telefonoMapperCli::fromDomainToAdapterCli).toList();
		if (telefonos.isEmpty()) {
			System.out.println("No hay telefonos registrados en " + motorActual());
		}
		telefonos.forEach(System.out::println);
	}

	/**
	 * Busca un teléfono e imprime el resultado o el motivo por el que no existe.
	 *
	 * @param numero número del teléfono
	 */
	public void buscar(String numero) {
		try {
			System.out.println(telefonoMapperCli.fromDomainToAdapterCli(phoneInputPort.findOne(numero)));
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Crea un teléfono e imprime el resultado o el motivo por el que no se pudo crear.
	 *
	 * @param telefono datos leídos de la consola
	 */
	public void crear(TelefonoModelCli telefono) {
		try {
			System.out.println("Creado: " + telefonoMapperCli
					.fromDomainToAdapterCli(phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	/**
	 * Edita el operador o el dueño de un teléfono e imprime el resultado.
	 *
	 * @param telefono datos nuevos, con el número del teléfono a editar
	 */
	public void editar(TelefonoModelCli telefono) {
		try {
			System.out.println("Editado: " + telefonoMapperCli.fromDomainToAdapterCli(
					phoneInputPort.edit(telefono.getNumero(), telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	/**
	 * Elimina un teléfono e imprime el resultado.
	 *
	 * @param numero número del teléfono
	 */
	public void eliminar(String numero) {
		try {
			boolean eliminado = Boolean.TRUE.equals(phoneInputPort.drop(numero));
			System.out.println(eliminado ? "Telefono " + numero + " eliminado"
					: "El telefono " + numero + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Imprime el número de teléfonos del motor en uso.
	 */
	public void contar() {
		System.out.println("Total de telefonos en " + motorActual() + ": " + phoneInputPort.count());
	}
}

package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada por consola para personas.
 *
 * Recibe las dos implementaciones del puerto de salida y crea el caso de uso con
 * la del motor que el usuario elige en el menú, sin reiniciar la aplicación.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	/** Adaptador de salida de personas en MariaDB. */
	private final PersonOutputPort personOutputPortMaria;

	/** Adaptador de salida de personas en MongoDB. */
	private final PersonOutputPort personOutputPortMongo;

	/** Adaptador de salida de teléfonos en MariaDB. */
	private final PhoneOutputPort phoneOutputPortMaria;

	/** Adaptador de salida de teléfonos en MongoDB. */
	private final PhoneOutputPort phoneOutputPortMongo;

	/** Adaptador de salida de estudios en MariaDB. */
	private final StudyOutputPort studyOutputPortMaria;

	/** Adaptador de salida de estudios en MongoDB. */
	private final StudyOutputPort studyOutputPortMongo;

	/** Adaptador de salida de profesiones en MariaDB. */
	private final ProfessionOutputPort professionOutputPortMaria;

	/** Adaptador de salida de profesiones en MongoDB. */
	private final ProfessionOutputPort professionOutputPortMongo;

	/** Conversor del modelo de consola de persona. */
	private final PersonaMapperCli personaMapperCli;

	/** Conversor del modelo de consola de teléfono. */
	private final TelefonoMapperCli telefonoMapperCli;

	/** Conversor del modelo de consola de estudio. */
	private final EstudioMapperCli estudioMapperCli;

	/** Caso de uso de personas conectado al motor elegido en el menú. */
	private PersonInputPort personInputPort;

	/** Caso de uso de teléfonos conectado al motor elegido, para agregación desde persona. */
	private PhoneInputPort phoneInputPort;

	/** Caso de uso de estudios conectado al motor elegido, para agregación desde persona. */
	private StudyInputPort studyInputPort;

	/** Motor elegido en el menú. */
	private DatabaseOption motor;

	/**
	 * Recibe los adaptadores de salida de ambos motores y los conversores de consola.
	 *
	 * @param personOutputPortMaria personas en MariaDB
	 * @param personOutputPortMongo personas en MongoDB
	 * @param phoneOutputPortMaria teléfonos en MariaDB
	 * @param phoneOutputPortMongo teléfonos en MongoDB
	 * @param studyOutputPortMaria estudios en MariaDB
	 * @param studyOutputPortMongo estudios en MongoDB
	 * @param professionOutputPortMaria profesiones en MariaDB
	 * @param professionOutputPortMongo profesiones en MongoDB
	 * @param personaMapperCli conversor de personas
	 * @param telefonoMapperCli conversor de teléfonos
	 * @param estudioMapperCli conversor de estudios
	 */
	public PersonaInputAdapterCli(@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phoneOutputPortMaria,
			@Qualifier("phoneOutputAdapterMongo") PhoneOutputPort phoneOutputPortMongo,
			@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyOutputPortMaria,
			@Qualifier("studyOutputAdapterMongo") StudyOutputPort studyOutputPortMongo,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			PersonaMapperCli personaMapperCli, TelefonoMapperCli telefonoMapperCli,
			EstudioMapperCli estudioMapperCli) {
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.phoneOutputPortMaria = phoneOutputPortMaria;
		this.phoneOutputPortMongo = phoneOutputPortMongo;
		this.studyOutputPortMaria = studyOutputPortMaria;
		this.studyOutputPortMongo = studyOutputPortMongo;
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.personaMapperCli = personaMapperCli;
		this.telefonoMapperCli = telefonoMapperCli;
		this.estudioMapperCli = estudioMapperCli;
	}

	/**
	 * Conecta el adaptador al motor elegido creando los casos de uso con sus puertos de
	 * salida. Las operaciones siguientes usan ese motor hasta que se elija otro.
	 *
	 * @param dbOption motor elegido: MARIA o MONGO
	 * @throws InvalidOptionException si el motor no es válido
	 */
	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		motor = DatabaseOption.from(dbOption);
		if (motor == DatabaseOption.MARIA) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
			studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
		} else {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
			studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
		}
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
	 * Imprime las personas del motor en uso.
	 */
	public void historial() {
		log.info("Into historial PersonaEntity in Input Adapter");
		List<PersonaModelCli> personas = personInputPort.findAll().stream()
				.map(personaMapperCli::fromDomainToAdapterCli).toList();
		if (personas.isEmpty()) {
			System.out.println("No hay personas registradas en " + motorActual());
		}
		personas.forEach(System.out::println);
	}

	/**
	 * Busca una persona e imprime sus datos junto con sus teléfonos y estudios
	 * agregados, o el motivo por el que no existe.
	 *
	 * @param cc cédula de la persona
	 */
	public void buscar(Integer cc) {
		try {
			Person person = personInputPort.findOne(cc);
			System.out.println(personaMapperCli.fromDomainToAdapterCli(person));
			List<Phone> phones = person.getPhoneNumbers();
			if (phones != null && !phones.isEmpty()) {
				System.out.println("  Telefonos (" + phones.size() + "):");
				phones.stream().map(telefonoMapperCli::fromDomainToAdapterCli)
						.forEach(phone -> System.out.println("    " + phone));
			}
			List<Study> studies = person.getStudies();
			if (studies != null && !studies.isEmpty()) {
				System.out.println("  Estudios (" + studies.size() + "):");
				studies.stream().map(estudioMapperCli::fromDomainToAdapterCli)
						.forEach(study -> System.out.println("    " + study));
			}
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Verifica si una persona existe en el motor actual antes de pedir los datos de un
	 * teléfono o estudio que se le va a agregar.
	 *
	 * @param cc cédula de la persona
	 * @return {@code true} si la persona existe
	 */
	public boolean verificarPersonaExiste(Integer cc) {
		try {
			Person person = personInputPort.findOne(cc);
			System.out.println("Persona seleccionada: " + personaMapperCli.fromDomainToAdapterCli(person));
			return true;
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Agrega un teléfono directamente a una persona existente en el motor en uso.
	 *
	 * @param telefono datos del teléfono con la cédula del dueño
	 */
	public void agregarTelefono(TelefonoModelCli telefono) {
		try {
			Phone creado = phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(telefono));
			System.out.println("Telefono agregado a " + telefono.getCcDuenio() + ": "
					+ telefonoMapperCli.fromDomainToAdapterCli(creado));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo agregar el telefono: " + e.getMessage());
		}
	}

	/**
	 * Agrega un estudio directamente a una persona existente en el motor en uso.
	 *
	 * @param estudio datos del estudio con la cédula de la persona y la profesión
	 */
	public void agregarEstudio(EstudioModelCli estudio) {
		try {
			Study creado = studyInputPort.create(estudioMapperCli.fromAdapterCliToDomain(estudio));
			System.out.println("Estudio agregado a " + estudio.getCcPersona() + ": "
					+ estudioMapperCli.fromDomainToAdapterCli(creado));
		} catch (AlreadyExistException | NoExistException | InvalidDataException e) {
			System.out.println("No se pudo agregar el estudio: " + e.getMessage());
		}
	}

	/**
	 * Crea una persona e imprime el resultado o el motivo por el que no se pudo crear.
	 *
	 * @param persona datos leídos de la consola
	 */
	public void crear(PersonaModelCli persona) {
		try {
			System.out.println("Creada: " + personaMapperCli
					.fromDomainToAdapterCli(personInputPort.create(personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (AlreadyExistException | InvalidDataException e) {
			System.out.println("No se pudo crear: " + e.getMessage());
		}
	}

	/**
	 * Edita una persona e imprime el resultado.
	 *
	 * @param persona datos nuevos, con la cédula de la persona a editar
	 */
	public void editar(PersonaModelCli persona) {
		try {
			System.out.println("Editada: " + personaMapperCli.fromDomainToAdapterCli(
					personInputPort.edit(persona.getCc(), personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (NoExistException | InvalidDataException e) {
			System.out.println("No se pudo editar: " + e.getMessage());
		}
	}

	/**
	 * Elimina una persona con sus teléfonos y estudios e imprime el resultado.
	 *
	 * @param cc cédula de la persona
	 */
	public void eliminar(Integer cc) {
		try {
			boolean eliminada = Boolean.TRUE.equals(personInputPort.drop(cc));
			System.out.println(eliminada ? "Persona " + cc + " eliminada (con sus telefonos y estudios)"
					: "La persona " + cc + " no se pudo eliminar");
		} catch (NoExistException e) {
			System.out.println("No existe: " + e.getMessage());
		}
	}

	/**
	 * Imprime el número de personas del motor en uso.
	 */
	public void contar() {
		System.out.println("Total de personas en " + motorActual() + ": " + personInputPort.count());
	}

	/**
	 * Imprime los teléfonos de una persona.
	 *
	 * @param cc cédula de la persona
	 */
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

	/**
	 * Imprime los estudios de una persona.
	 *
	 * @param cc cédula de la persona
	 */
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

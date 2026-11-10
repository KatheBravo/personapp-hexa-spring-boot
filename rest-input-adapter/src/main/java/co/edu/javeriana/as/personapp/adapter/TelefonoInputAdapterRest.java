package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
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
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

	private final PhoneOutputPort phoneOutputPortMaria;
	private final PhoneOutputPort phoneOutputPortMongo;
	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final TelefonoMapperRest telefonoMapperRest;

	public TelefonoInputAdapterRest(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phoneOutputPortMaria,
			@Qualifier("phoneOutputAdapterMongo") PhoneOutputPort phoneOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			TelefonoMapperRest telefonoMapperRest) {
		this.phoneOutputPortMaria = phoneOutputPortMaria;
		this.phoneOutputPortMongo = phoneOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.telefonoMapperRest = telefonoMapperRest;
	}

	/** Ambos puertos (telefonos y personas) siempre del MISMO motor. */
	private PhoneInputPort setPhoneOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	public List<TelefonoResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial TelefonoEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setPhoneOutputPortInjection(db).findAll().stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel())).toList();
	}

	public TelefonoResponse buscarTelefono(String database, String number)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Phone phone = setPhoneOutputPortInjection(db).findOne(number);
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	public TelefonoResponse crearTelefono(TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Phone phone = setPhoneOutputPortInjection(db).create(telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	public TelefonoResponse editarTelefono(String number, TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setNumber(number);
		Phone phone = setPhoneOutputPortInjection(db).edit(number, telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, db.getLabel());
	}

	public Response eliminarTelefono(String database, String number) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setPhoneOutputPortInjection(db).drop(number);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Phone " + number + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel(),
				LocalDateTime.now());
	}

	public Integer contarTelefonos(String database) throws InvalidOptionException {
		return setPhoneOutputPortInjection(DatabaseOption.from(database)).count();
	}
}

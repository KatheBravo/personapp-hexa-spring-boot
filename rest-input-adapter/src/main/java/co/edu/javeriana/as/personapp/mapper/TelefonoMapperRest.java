package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {

	public TelefonoResponse fromDomainToAdapterRest(Phone phone, String database) {
		Person owner = phone.getOwner();
		return new TelefonoResponse(
				phone.getNumber(),
				phone.getCompany(),
				owner != null ? RestValues.text(owner.getIdentification()) : null,
				owner != null ? fullName(owner) : null,
				database,
				"OK");
	}

	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/** El duenio llega solo con su cedula; el caso de uso verifica que exista. */
	public Phone fromAdapterToDomain(TelefonoRequest request) throws InvalidDataException {
		Phone phone = new Phone();
		phone.setNumber(RestValues.requiredText(request.getNumber(), "number"));
		phone.setCompany(RestValues.requiredText(request.getCompany(), "company"));
		Person owner = new Person();
		owner.setIdentification(RestValues.requiredInteger(request.getOwnerId(), "ownerId"));
		phone.setOwner(owner);
		return phone;
	}
}

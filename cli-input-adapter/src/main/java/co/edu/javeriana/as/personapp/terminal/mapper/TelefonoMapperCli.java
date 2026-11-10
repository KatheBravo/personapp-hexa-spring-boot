package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		Person owner = phone.getOwner();
		return new TelefonoModelCli(phone.getNumber(), phone.getCompany(),
				owner != null ? owner.getIdentification() : null, owner != null ? fullName(owner) : null);
	}

	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/** El duenio va solo con su cedula; el caso de uso verifica que exista. */
	public Phone fromAdapterCliToDomain(TelefonoModelCli telefonoModelCli) {
		Phone phone = new Phone();
		phone.setNumber(telefonoModelCli.getNumero());
		phone.setCompany(telefonoModelCli.getOperador());
		Person owner = new Person();
		owner.setIdentification(telefonoModelCli.getCcDuenio());
		phone.setOwner(owner);
		return phone;
	}
}

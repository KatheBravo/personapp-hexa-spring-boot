package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

/**
 * Convierte teléfonos entre el dominio y el modelo de consola.
 */
@Mapper
public class TelefonoMapperCli {

	/**
	 * Convierte un teléfono del dominio en el modelo de consola.
	 *
	 * @param phone teléfono del dominio
	 * @return modelo con la cédula y el nombre del dueño
	 */
	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		Person owner = phone.getOwner();
		return new TelefonoModelCli(phone.getNumber(), phone.getCompany(),
				owner != null ? owner.getIdentification() : null, owner != null ? fullName(owner) : null);
	}

	/**
	 * Une el nombre y el apellido de una persona.
	 *
	 * @param person persona
	 * @return nombre completo, sin espacios sobrantes
	 */
	private String fullName(Person person) {
		String firstName = person.getFirstName() != null ? person.getFirstName() : "";
		String lastName = person.getLastName() != null ? person.getLastName() : "";
		return (firstName + " " + lastName).trim();
	}

	/**
	 * Convierte los datos leídos de la consola en teléfono del dominio. El dueño queda
	 * solo con su cédula; el caso de uso verifica que exista.
	 *
	 * @param telefonoModelCli datos leídos de la consola
	 * @return teléfono del dominio
	 */
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

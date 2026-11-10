package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

/**
 * Convierte teléfonos entre el dominio y los modelos de la API REST.
 */
@Mapper
public class TelefonoMapperRest {

	/**
	 * Convierte un teléfono del dominio en respuesta REST.
	 *
	 * @param phone teléfono del dominio
	 * @param database nombre del motor que atendió la operación
	 * @return respuesta con los datos del teléfono y el nombre del dueño
	 */
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
	 * Convierte la petición REST en teléfono del dominio. El dueño queda solo con su
	 * cédula; el caso de uso verifica que exista.
	 *
	 * @param request datos recibidos en el cuerpo
	 * @return teléfono del dominio
	 * @throws InvalidDataException si falta un campo obligatorio o la cédula del dueño no es
	 *         numérica
	 */
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

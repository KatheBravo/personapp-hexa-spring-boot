package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.PersonaRequest;

/**
 * Respuesta REST de una persona: los campos de {@link PersonaRequest} más el estado
 * de la operación. En {@code database} se informa el motor que la atendió.
 */
public class PersonaResponse extends PersonaRequest {

	/** Estado de la operación. */
	private String status;

	/**
	 * Crea la respuesta con los datos de la persona.
	 *
	 * @param dni cédula
	 * @param firstName nombre
	 * @param lastName apellido
	 * @param age edad; puede ser nula
	 * @param sex género, M o F
	 * @param database motor que atendió la operación
	 * @param status estado de la operación
	 */
	public PersonaResponse(String dni, String firstName, String lastName, String age, String sex, String database,
			String status) {
		super(dni, firstName, lastName, age, sex, database);
		this.status = status;
	}

	/** @return estado de la operación */
	public String getStatus() {
		return status;
	}

	/** @param status estado de la operación */
	public void setStatus(String status) {
		this.status = status;
	}

}

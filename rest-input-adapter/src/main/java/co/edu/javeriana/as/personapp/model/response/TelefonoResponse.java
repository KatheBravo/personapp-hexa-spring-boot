package co.edu.javeriana.as.personapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta REST de un teléfono, con el nombre de su dueño.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoResponse {
	/** Número del teléfono. */
	private String number;

	/** Operador. */
	private String company;

	/** Cédula del dueño. */
	private String ownerId;

	/** Nombre completo del dueño. */
	private String ownerName;

	/** Motor que atendió la operación: MariaDB o MongoDB. */
	private String database;

	/** Estado de la operación. */
	private String status;
}

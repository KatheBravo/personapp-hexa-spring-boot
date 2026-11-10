package co.edu.javeriana.as.personapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta REST de un estudio, con los nombres de la persona y de la profesión.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioResponse {
	/** Cédula de la persona. */
	private String personId;

	/** Nombre completo de la persona. */
	private String personName;

	/** Identificador de la profesión. */
	private String professionId;

	/** Nombre de la profesión. */
	private String professionName;

	/** Fecha de grado en formato yyyy-MM-dd; puede ser nula. */
	private String graduationDate;

	/** Universidad. */
	private String universityName;

	/** Motor que atendió la operación: MariaDB o MongoDB. */
	private String database;

	/** Estado de la operación. */
	private String status;
}

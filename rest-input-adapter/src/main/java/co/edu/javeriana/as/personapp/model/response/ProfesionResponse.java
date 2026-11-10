package co.edu.javeriana.as.personapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta REST de una profesión.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionResponse {
	/** Identificador de la profesión. */
	private String id;

	/** Nombre. */
	private String name;

	/** Descripción. */
	private String description;

	/** Motor que atendió la operación: MariaDB o MongoDB. */
	private String database;

	/** Estado de la operación. */
	private String status;
}

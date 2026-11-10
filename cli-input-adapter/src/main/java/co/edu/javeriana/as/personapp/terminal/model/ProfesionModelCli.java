package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de una profesión tal como se leen y se muestran en la consola.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionModelCli {
	/** Identificador. */
	private Integer id;

	/** Nombre. */
	private String nombre;

	/** Descripción; puede ser nula. */
	private String descripcion;

	/**
	 * Representación en una línea para la consola.
	 *
	 * @return texto con la forma {@code Profesion[id=..., nombre=..., descripcion=...]}
	 */
	@Override
	public String toString() {
		return String.format("Profesion[id=%d, nombre=%s, descripcion=%s]", id, nombre,
				descripcion != null && !descripcion.isBlank() ? descripcion : "-");
	}
}

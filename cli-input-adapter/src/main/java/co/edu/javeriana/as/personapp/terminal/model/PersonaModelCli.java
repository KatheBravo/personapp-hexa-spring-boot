package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de una persona tal como se leen y se muestran en la consola.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaModelCli {
	/** Cédula. */
	private Integer cc;

	/** Nombre. */
	private String nombre;

	/** Apellido. */
	private String apellido;

	/** Género: M o F. */
	private String genero;

	/** Edad; puede ser nula. */
	private Integer edad;

	/**
	 * Representación en una línea para la consola.
	 *
	 * @return texto con la forma {@code Persona[cc=..., nombre=..., genero=..., edad=...]}
	 */
	@Override
	public String toString() {
		return String.format("Persona[cc=%d, nombre=%s %s, genero=%s, edad=%s]", cc, nombre, apellido, genero,
				edad != null ? edad : "-");
	}
}

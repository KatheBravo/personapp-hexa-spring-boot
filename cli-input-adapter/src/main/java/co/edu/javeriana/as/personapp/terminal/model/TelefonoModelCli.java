package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de un teléfono tal como se leen y se muestran en la consola.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoModelCli {
	/** Número del teléfono. */
	private String numero;

	/** Operador. */
	private String operador;

	/** Cédula del dueño. */
	private Integer ccDuenio;

	/** Nombre completo del dueño; solo para mostrar. */
	private String nombreDuenio;

	/**
	 * Representación en una línea para la consola.
	 *
	 * @return texto con la forma {@code Telefono[numero=..., operador=..., duenio=...]}
	 */
	@Override
	public String toString() {
		return String.format("Telefono[numero=%s, operador=%s, duenio=%d %s]", numero, operador, ccDuenio,
				nombreDuenio != null ? "(" + nombreDuenio + ")" : "");
	}
}

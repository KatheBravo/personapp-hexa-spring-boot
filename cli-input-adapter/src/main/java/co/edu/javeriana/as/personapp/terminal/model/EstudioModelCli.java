package co.edu.javeriana.as.personapp.terminal.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de un estudio tal como se leen y se muestran en la consola.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	/** Cédula de la persona. */
	private Integer ccPersona;

	/** Nombre completo de la persona; solo para mostrar. */
	private String nombrePersona;

	/** Identificador de la profesión. */
	private Integer idProfesion;

	/** Nombre de la profesión; solo para mostrar. */
	private String nombreProfesion;

	/** Fecha de grado; puede ser nula. */
	private LocalDate fechaGrado;

	/** Universidad; puede ser nula. */
	private String universidad;

	/**
	 * Representación en una línea para la consola.
	 *
	 * @return texto con la forma {@code Estudio[persona=..., profesion=..., fecha=..., universidad=...]}
	 */
	@Override
	public String toString() {
		return String.format("Estudio[persona=%d %s, profesion=%d %s, fecha=%s, universidad=%s]", ccPersona,
				nombrePersona != null ? "(" + nombrePersona + ")" : "", idProfesion,
				nombreProfesion != null ? "(" + nombreProfesion + ")" : "", fechaGrado != null ? fechaGrado : "-",
				universidad != null && !universidad.isBlank() ? universidad : "-");
	}
}

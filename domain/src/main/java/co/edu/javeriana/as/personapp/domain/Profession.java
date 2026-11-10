package co.edu.javeriana.as.personapp.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Profesión del modelo de negocio, identificada por un número entero.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Profession {
	/** Identificador de la profesión. */
	@NonNull
	private Integer identification;

	/** Nombre, obligatorio, de máximo 90 caracteres. */
	@NonNull
	private String name;

	/** Descripción de la profesión; es opcional. */
	private String description;

	/** Estudios registrados en esta profesión. */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Study> studies;

	/**
	 * Verifica que el identificador sea un número positivo.
	 *
	 * @return {@code true} si el identificador está informado y es mayor que cero
	 */
	public Boolean isValidIdentification() {
		return this.identification != null && this.identification > 0;
	}

	/**
	 * Verifica que el nombre esté informado y tenga máximo 90 caracteres.
	 *
	 * @return {@code true} si el nombre es válido
	 */
	public Boolean isValidName() {
		return this.name != null && !this.name.isBlank() && this.name.length() <= 90;
	}
}

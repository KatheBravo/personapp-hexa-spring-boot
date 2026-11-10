package co.edu.javeriana.as.personapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Teléfono del modelo de negocio. El número lo identifica y siempre pertenece a
 * una persona.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
	/** Número del teléfono; es su identificador. */
	@NonNull
	private String number;

	/** Operador, obligatorio, de máximo 45 caracteres. */
	@NonNull
	private String company;

	/** Persona dueña del teléfono. */
	@NonNull
	private Person owner;

	/**
	 * Verifica que el número tenga solo dígitos, entre 7 (fijo) y 15 (máximo
	 * internacional).
	 *
	 * @return {@code true} si el número tiene el formato esperado
	 */
	public Boolean isValidNumber() {
		return this.number != null && this.number.matches("\\d{7,15}");
	}

	/**
	 * Verifica que el operador esté informado y tenga máximo 45 caracteres.
	 *
	 * @return {@code true} si el operador es válido
	 */
	public Boolean isValidCompany() {
		return this.company != null && !this.company.isBlank() && this.company.length() <= 45;
	}
}

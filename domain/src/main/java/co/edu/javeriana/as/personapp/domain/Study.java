package co.edu.javeriana.as.personapp.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Estudio de una persona en una profesión.
 *
 * La pareja persona y profesión lo identifica, igual que la llave primaria
 * compuesta de la tabla {@code estudios}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Study {
	/** Persona que realizó el estudio. */
	@NonNull
	private Person person;

	/** Profesión estudiada. */
	@NonNull
	private Profession profession;

	/** Fecha de grado; es opcional. */
	private LocalDate graduationDate;

	/** Universidad donde se realizó el estudio; opcional, de máximo 50 caracteres. */
	private String universityName;

	/**
	 * Verifica la fecha de grado. Es opcional, pero no puede ser posterior a la
	 * fecha actual.
	 *
	 * @return {@code true} si la fecha es nula o no está en el futuro
	 */
	public Boolean isValidGraduationDate() {
		return this.graduationDate == null || !this.graduationDate.isAfter(LocalDate.now());
	}

	/**
	 * Verifica que la universidad, si está informada, tenga máximo 50 caracteres.
	 *
	 * @return {@code true} si el nombre es nulo o cabe en la longitud máxima
	 */
	public Boolean isValidUniversityName() {
		return this.universityName == null || this.universityName.length() <= 50;
	}
}

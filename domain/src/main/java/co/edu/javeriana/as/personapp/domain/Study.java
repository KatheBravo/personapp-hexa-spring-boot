package co.edu.javeriana.as.personapp.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Study {
	@NonNull
	private Person person;
	@NonNull
	private Profession profession;
	private LocalDate graduationDate;
	private String universityName;

	/** La fecha de grado es opcional, pero no puede estar en el futuro. */
	public Boolean isValidGraduationDate() {
		return this.graduationDate == null || !this.graduationDate.isAfter(LocalDate.now());
	}

	/** La universidad es opcional y tiene maximo 50 caracteres (modelo de datos). */
	public Boolean isValidUniversityName() {
		return this.universityName == null || this.universityName.length() <= 50;
	}
}

package co.edu.javeriana.as.personapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
	@NonNull
	private String number;
	@NonNull
	private String company;
	@NonNull
	private Person owner;

	/** Un telefono solo tiene digitos: entre 7 (fijo) y 15 (maximo internacional). */
	public Boolean isValidNumber() {
		return this.number != null && this.number.matches("\\d{7,15}");
	}

	/** El operador es obligatorio y tiene maximo 45 caracteres (modelo de datos). */
	public Boolean isValidCompany() {
		return this.company != null && !this.company.isBlank() && this.company.length() <= 45;
	}
}

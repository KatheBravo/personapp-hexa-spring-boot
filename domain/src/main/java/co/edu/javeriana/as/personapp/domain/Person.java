package co.edu.javeriana.as.personapp.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Person {
	@NonNull
	private Integer identification;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private Gender gender;
	private Integer age;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Phone> phoneNumbers;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Study> studies;

	/** La edad es opcional; si viene, no puede ser negativa. */
	public Boolean isValidAge() {
		return this.age == null || this.age >= 0;
	}

	/** La cedula debe ser un numero positivo. */
	public Boolean isValidIdentification() {
		return this.identification != null && this.identification > 0;
	}

	/** Nombre y apellido son obligatorios y tienen maximo 45 caracteres (modelo de datos). */
	public Boolean isValidName() {
		return isFilled(this.firstName, 45) && isFilled(this.lastName, 45);
	}

	private static boolean isFilled(String value, int maxLength) {
		return value != null && !value.isBlank() && value.length() <= maxLength;
	}
}

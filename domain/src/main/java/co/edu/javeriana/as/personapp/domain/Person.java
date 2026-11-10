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
 * Persona del modelo de negocio.
 *
 * No depende de ninguna tecnología de persistencia: cada adaptador la convierte
 * desde y hacia su propio modelo. La cédula identifica a la persona. Los
 * teléfonos y estudios se excluyen de {@code toString}, {@code equals} y
 * {@code hashCode} para no recorrer las relaciones en ciclo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Person {
	/** Cédula de la persona; es su identificador. */
	@NonNull
	private Integer identification;

	/** Nombre, obligatorio, de máximo 45 caracteres. */
	@NonNull
	private String firstName;

	/** Apellido, obligatorio, de máximo 45 caracteres. */
	@NonNull
	private String lastName;

	/** Género, obligatorio. */
	@NonNull
	private Gender gender;

	/** Edad en años; es opcional. */
	private Integer age;

	/** Teléfonos registrados a nombre de la persona. */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Phone> phoneNumbers;

	/** Estudios realizados por la persona. */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Study> studies;

	/**
	 * Verifica la edad. Es opcional, pero si está informada no puede ser negativa.
	 *
	 * @return {@code true} si la edad es nula o mayor o igual a cero
	 */
	public Boolean isValidAge() {
		return this.age == null || this.age >= 0;
	}

	/**
	 * Verifica que la cédula sea un número positivo.
	 *
	 * @return {@code true} si la cédula está informada y es mayor que cero
	 */
	public Boolean isValidIdentification() {
		return this.identification != null && this.identification > 0;
	}

	/**
	 * Verifica que el nombre y el apellido estén informados y tengan máximo 45
	 * caracteres, como en el modelo de datos.
	 *
	 * @return {@code true} si ambos campos son válidos
	 */
	public Boolean isValidName() {
		return isFilled(this.firstName, 45) && isFilled(this.lastName, 45);
	}

	/**
	 * Indica si un texto tiene contenido y no supera una longitud máxima.
	 *
	 * @param value texto a revisar
	 * @param maxLength longitud máxima permitida
	 * @return {@code true} si el texto no es nulo, no está en blanco y cabe en la longitud
	 */
	private static boolean isFilled(String value, int maxLength) {
		return value != null && !value.isBlank() && value.length() <= maxLength;
	}
}

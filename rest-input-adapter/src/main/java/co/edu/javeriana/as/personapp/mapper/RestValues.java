package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Gender;

/**
 * Utilidades para convertir los textos del JSON a los tipos del dominio.
 *
 * Si un valor no se puede convertir se lanza {@link InvalidDataException}, que la
 * API responde con HTTP 400 y un mensaje que indica el campo con error.
 */
public final class RestValues {

	/** Clase de utilidades; no se instancia. */
	private RestValues() {
	}

	/**
	 * Convierte un texto obligatorio en entero.
	 *
	 * @param value texto recibido
	 * @param field nombre del campo, para el mensaje de error
	 * @return el número convertido
	 * @throws InvalidDataException si el texto está vacío o no es un entero
	 */
	public static Integer requiredInteger(String value, String field) throws InvalidDataException {
		if (value == null || value.isBlank()) {
			throw new InvalidDataException("The field '" + field + "' is required");
		}
		return parseInteger(value, field);
	}

	/**
	 * Convierte un texto opcional en entero.
	 *
	 * @param value texto recibido; puede ser nulo o vacío
	 * @param field nombre del campo, para el mensaje de error
	 * @return el número convertido, o {@code null} si no se informó
	 * @throws InvalidDataException si el texto no es un entero
	 */
	public static Integer optionalInteger(String value, String field) throws InvalidDataException {
		return value == null || value.isBlank() ? null : parseInteger(value, field);
	}

	/**
	 * Convierte un texto en entero sin tener en cuenta los espacios de los extremos.
	 *
	 * @param value texto a convertir
	 * @param field nombre del campo, para el mensaje de error
	 * @return el número convertido
	 * @throws InvalidDataException si el texto no es un entero
	 */
	private static Integer parseInteger(String value, String field) throws InvalidDataException {
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			throw new InvalidDataException("The field '" + field + "' must be an integer number: " + value);
		}
	}

	/**
	 * Valida un texto obligatorio y le quita los espacios de los extremos.
	 *
	 * @param value texto recibido
	 * @param field nombre del campo, para el mensaje de error
	 * @return el texto sin espacios en los extremos
	 * @throws InvalidDataException si el texto es nulo o está vacío
	 */
	public static String requiredText(String value, String field) throws InvalidDataException {
		if (value == null || value.isBlank()) {
			throw new InvalidDataException("The field '" + field + "' is required");
		}
		return value.trim();
	}

	/**
	 * Normaliza un texto opcional.
	 *
	 * @param value texto recibido
	 * @return el texto sin espacios en los extremos, o {@code null} si está vacío
	 */
	public static String optionalText(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	/**
	 * Convierte un texto opcional con formato {@code yyyy-MM-dd} en fecha.
	 *
	 * @param value texto recibido
	 * @param field nombre del campo, para el mensaje de error
	 * @return la fecha, o {@code null} si no se informó
	 * @throws InvalidDataException si el texto no tiene el formato esperado
	 */
	public static LocalDate optionalDate(String value, String field) throws InvalidDataException {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw new InvalidDataException("The field '" + field + "' must have the format yyyy-MM-dd: " + value);
		}
	}

	/**
	 * Convierte el género recibido en el valor del dominio. Acepta M, F, MALE o FEMALE
	 * sin importar mayúsculas.
	 *
	 * @param value texto recibido
	 * @return género del dominio
	 * @throws InvalidDataException si el valor falta o no es un género válido
	 */
	public static Gender gender(String value) throws InvalidDataException {
		String sex = requiredText(value, "sex").toUpperCase();
		if (sex.equals("M") || sex.equals("MALE")) {
			return Gender.MALE;
		}
		if (sex.equals("F") || sex.equals("FEMALE")) {
			return Gender.FEMALE;
		}
		throw new InvalidDataException("The field 'sex' must be M or F: " + value);
	}

	/**
	 * Convierte el género del dominio al texto que usa la API.
	 *
	 * @param gender género del dominio
	 * @return {@code M}, {@code F} o {@code null} si es otro
	 */
	public static String sex(Gender gender) {
		return gender == Gender.MALE ? "M" : gender == Gender.FEMALE ? "F" : null;
	}

	/**
	 * Convierte un valor a texto para la respuesta.
	 *
	 * @param value valor a convertir; puede ser nulo
	 * @return el texto del valor, o {@code null}
	 */
	public static String text(Object value) {
		return value != null ? value.toString() : null;
	}
}

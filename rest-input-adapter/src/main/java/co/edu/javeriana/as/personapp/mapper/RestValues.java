package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Gender;

/**
 * Conversiones de los textos que llegan en el JSON a los tipos del dominio.
 * Si un valor no se puede convertir se lanza InvalidDataException (HTTP 400)
 * con un mensaje claro, en lugar de un error 500.
 */
public final class RestValues {

	private RestValues() {
	}

	public static Integer requiredInteger(String value, String field) throws InvalidDataException {
		if (value == null || value.isBlank()) {
			throw new InvalidDataException("The field '" + field + "' is required");
		}
		return parseInteger(value, field);
	}

	public static Integer optionalInteger(String value, String field) throws InvalidDataException {
		return value == null || value.isBlank() ? null : parseInteger(value, field);
	}

	private static Integer parseInteger(String value, String field) throws InvalidDataException {
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			throw new InvalidDataException("The field '" + field + "' must be an integer number: " + value);
		}
	}

	public static String requiredText(String value, String field) throws InvalidDataException {
		if (value == null || value.isBlank()) {
			throw new InvalidDataException("The field '" + field + "' is required");
		}
		return value.trim();
	}

	public static String optionalText(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

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

	public static String sex(Gender gender) {
		return gender == Gender.MALE ? "M" : gender == Gender.FEMALE ? "F" : null;
	}

	public static String text(Object value) {
		return value != null ? value.toString() : null;
	}
}

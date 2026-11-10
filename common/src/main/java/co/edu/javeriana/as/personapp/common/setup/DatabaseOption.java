package co.edu.javeriana.as.personapp.common.setup;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;

/**
 * Motores de persistencia disponibles.
 *
 * El motor se elige en cada operación, en tiempo de ejecución, sin recompilar ni
 * reiniciar la aplicación.
 */
public enum DatabaseOption {
	MONGO("MongoDB"), MARIA("MariaDB");

	/** Nombre del motor para mostrar al usuario. */
	private final String label;

	/**
	 * Crea la opción con su nombre visible.
	 *
	 * @param label nombre del motor para mostrar
	 */
	DatabaseOption(String label) {
		this.label = label;
	}

	/**
	 * Devuelve el nombre del motor para respuestas y mensajes.
	 *
	 * @return nombre visible, por ejemplo {@code MariaDB}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Convierte el texto recibido por un adaptador de entrada en una opción de motor.
	 *
	 * Acepta {@code MARIA}, {@code MONGO}, {@code MariaDB} o {@code MongoDB}, sin
	 * importar mayúsculas ni espacios al inicio o al final.
	 *
	 * @param value texto con el motor solicitado
	 * @return la opción correspondiente
	 * @throws InvalidOptionException si el texto es nulo o no corresponde a ningún motor
	 */
	public static DatabaseOption from(String value) throws InvalidOptionException {
		if (value != null) {
			String normalized = value.trim().toUpperCase();
			if (normalized.equals("MARIA") || normalized.equals("MARIADB")) {
				return MARIA;
			}
			if (normalized.equals("MONGO") || normalized.equals("MONGODB")) {
				return MONGO;
			}
		}
		throw new InvalidOptionException("Invalid database option: " + value + " (use MARIA or MONGO)");
	}
}

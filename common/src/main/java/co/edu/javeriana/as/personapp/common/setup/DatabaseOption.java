package co.edu.javeriana.as.personapp.common.setup;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;

/**
 * Motores de persistencia disponibles. La eleccion se hace en tiempo de
 * ejecucion (en caliente), no en tiempo de compilacion.
 */
public enum DatabaseOption {
	MONGO("MongoDB"), MARIA("MariaDB");

	private final String label;

	DatabaseOption(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Convierte el texto que llega desde un adaptador de entrada (REST o CLI) en
	 * una opcion valida. Acepta "MARIA", "MONGO", "MariaDB" o "MongoDB" sin
	 * importar mayusculas.
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

package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Indica que los datos de una entidad no cumplen las reglas del dominio, por
 * ejemplo una edad negativa o un nombre vacío.
 */
public class InvalidDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con la regla que no se cumple.
	 *
	 * @param mensaje descripción del error
	 */
	public InvalidDataException(String mensaje) {
		super(mensaje);
	}

}

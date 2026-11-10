package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Se lanza cuando los datos de una entidad de negocio no cumplen las reglas
 * del dominio (por ejemplo, una edad negativa o un nombre vacio).
 */
public class InvalidDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidDataException(String mensaje) {
		super(mensaje);
	}

}

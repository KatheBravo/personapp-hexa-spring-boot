package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Se lanza cuando se intenta crear un registro cuyo identificador ya existe.
 */
public class AlreadyExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public AlreadyExistException(String mensaje) {
		super(mensaje);
	}

}

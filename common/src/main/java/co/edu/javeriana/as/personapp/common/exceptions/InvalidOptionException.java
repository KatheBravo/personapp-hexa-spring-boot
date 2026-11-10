package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Indica que el motor de persistencia solicitado no es una opción válida.
 *
 * @see co.edu.javeriana.as.personapp.common.setup.DatabaseOption
 */
public class InvalidOptionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Crea la excepción con la opción recibida.
	 *
	 * @param mensaje descripción del error
	 */
	public InvalidOptionException(String mensaje) {
		super(mensaje);
	}

}

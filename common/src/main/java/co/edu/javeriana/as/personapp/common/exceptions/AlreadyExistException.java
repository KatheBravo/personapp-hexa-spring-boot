package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Indica que se intentó crear un registro cuyo identificador ya existe en el
 * motor de persistencia seleccionado.
 */
public class AlreadyExistException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el detalle del registro duplicado.
	 *
	 * @param mensaje descripción del error
	 */
	public AlreadyExistException(String mensaje) {
		super(mensaje);
	}

}

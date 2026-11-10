package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Indica que el registro buscado, o un registro relacionado que la operación
 * necesita, no existe en el motor de persistencia seleccionado.
 */
public class NoExistException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Crea la excepción con el registro que no se encontró.
	 *
	 * @param mensaje descripción del error
	 */
	public NoExistException(String mensaje) {
		super(mensaje);
	}

}

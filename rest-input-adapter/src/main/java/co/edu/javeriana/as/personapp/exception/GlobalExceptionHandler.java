package co.edu.javeriana.as.personapp.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Traduce las excepciones a respuestas HTTP con un cuerpo uniforme (status,
 * description y date). Así los controladores no manejan errores y el cliente
 * recibe siempre el mismo formato.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Responde 404 cuando el registro no existe.
	 *
	 * @param e excepción con el detalle
	 * @return respuesta HTTP 404
	 */
	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Response> handleNoExist(NoExistException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	/**
	 * Responde 409 cuando el registro ya existe.
	 *
	 * @param e excepción con el detalle
	 * @return respuesta HTTP 409
	 */
	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<Response> handleAlreadyExist(AlreadyExistException e) {
		return build(HttpStatus.CONFLICT, e.getMessage());
	}

	/**
	 * Responde 400 cuando el motor no es válido o los datos no cumplen las reglas del
	 * dominio.
	 *
	 * @param e excepción con el detalle
	 * @return respuesta HTTP 400
	 */
	@ExceptionHandler({ InvalidOptionException.class, InvalidDataException.class })
	public ResponseEntity<Response> handleBadRequest(Exception e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	/**
	 * Responde 400 cuando el cuerpo de la petición no es un JSON válido.
	 *
	 * @param e excepción de lectura del cuerpo
	 * @return respuesta HTTP 400
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Response> handleNotReadable(HttpMessageNotReadableException e) {
		return build(HttpStatus.BAD_REQUEST, "The request body is not a valid JSON for this operation");
	}

	/**
	 * Responde 400 cuando un parámetro de la ruta no tiene el tipo esperado, por
	 * ejemplo letras en una cédula.
	 *
	 * @param e excepción con el parámetro y el valor recibido
	 * @return respuesta HTTP 400
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Response> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		return build(HttpStatus.BAD_REQUEST, "Invalid value '" + e.getValue() + "' for parameter " + e.getName());
	}

	/**
	 * Atiende cualquier otra excepción. Los errores propios de Spring MVC, como una
	 * ruta inexistente o un método no permitido, conservan su código HTTP; el resto
	 * responde 500 y queda registrado en el log.
	 *
	 * @param e excepción no controlada
	 * @return respuesta con el código correspondiente
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleUnexpected(Exception e) {
		// Los errores de Spring MVC ya traen su código HTTP
		if (e instanceof ErrorResponse errorResponse) {
			HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
			return build(status != null ? status : HttpStatus.BAD_REQUEST, e.getMessage());
		}
		log.error("Unexpected error", e);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
	}

	/**
	 * Arma la respuesta de error y registra el detalle en el log.
	 *
	 * @param status código HTTP
	 * @param message descripción del error
	 * @return respuesta con el código y el cuerpo de error
	 */
	private ResponseEntity<Response> build(HttpStatus status, String message) {
		log.warn("{} -> {}", status, message);
		return ResponseEntity.status(status).body(new Response(status.name(), message, LocalDateTime.now()));
	}
}

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
 * Traduce las excepciones de negocio a codigos HTTP. Asi los controladores
 * quedan limpios y el cliente recibe siempre el mismo formato de error.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Response> handleNoExist(NoExistException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<Response> handleAlreadyExist(AlreadyExistException e) {
		return build(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler({ InvalidOptionException.class, InvalidDataException.class })
	public ResponseEntity<Response> handleBadRequest(Exception e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Response> handleNotReadable(HttpMessageNotReadableException e) {
		return build(HttpStatus.BAD_REQUEST, "The request body is not a valid JSON for this operation");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Response> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		return build(HttpStatus.BAD_REQUEST, "Invalid value '" + e.getValue() + "' for parameter " + e.getName());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleUnexpected(Exception e) {
		// Errores propios de Spring MVC (ruta inexistente, metodo no permitido...) traen su codigo HTTP
		if (e instanceof ErrorResponse errorResponse) {
			HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
			return build(status != null ? status : HttpStatus.BAD_REQUEST, e.getMessage());
		}
		log.error("Unexpected error", e);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
	}

	private ResponseEntity<Response> build(HttpStatus status, String message) {
		log.warn("{} -> {}", status, message);
		return ResponseEntity.status(status).body(new Response(status.name(), message, LocalDateTime.now()));
	}
}

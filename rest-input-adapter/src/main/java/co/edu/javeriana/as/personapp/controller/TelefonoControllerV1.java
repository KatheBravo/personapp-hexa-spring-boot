package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST, versión 1, en {@code /api/v1/telefono}.
 *
 * Recibe la petición HTTP y la delega en {@link TelefonoInputAdapterRest}. Las excepciones
 * de negocio las convierte en códigos HTTP
 * {@link co.edu.javeriana.as.personapp.exception.GlobalExceptionHandler}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telefono")
@Tag(name = "Telefono", description = "CRUD de telefonos en MariaDB o MongoDB")
public class TelefonoControllerV1 {

	/** Adaptador de entrada que ejecuta las operaciones. */
	private final TelefonoInputAdapterRest telefonoInputAdapterRest;

	/**
	 * Lista los teléfonos de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return teléfonos del motor
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Lista todos los telefonos")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		return telefonoInputAdapterRest.historial(database);
	}

	/**
	 * Cuenta los teléfonos de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return número de teléfonos
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Cuenta los telefonos")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return telefonoInputAdapterRest.contarTelefonos(database);
	}

	/**
	 * Busca un teléfono por número.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param numero número del teléfono
	 * @return el teléfono
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si el teléfono no existe (HTTP 404)
	 */
	@Operation(summary = "Busca un telefono por numero")
	@GetMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse telefono(@PathVariable("database") String database, @PathVariable("numero") String numero)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.buscarTelefono(database, numero);
	}

	/**
	 * Crea un teléfono. El motor va en el campo {@code database} del cuerpo y el
	 * dueño debe existir en ese motor.
	 *
	 * @param request datos del teléfono
	 * @return el teléfono creado (HTTP 201)
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws AlreadyExistException si el número ya existe (HTTP 409)
	 * @throws NoExistException si el dueño no existe (HTTP 404)
	 */
	@Operation(summary = "Crea un telefono (el duenio debe existir en el mismo motor)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse crearTelefono(@RequestBody TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		return telefonoInputAdapterRest.crearTelefono(request);
	}

	/**
	 * Edita el operador o el dueño de un teléfono.
	 *
	 * @param numero número del teléfono
	 * @param request datos nuevos y motor
	 * @return el teléfono actualizado
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws NoExistException si el teléfono o el dueño no existen (HTTP 404)
	 */
	@Operation(summary = "Edita un telefono (operador o duenio)")
	@PutMapping(path = "/{numero}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse editarTelefono(@PathVariable("numero") String numero, @RequestBody TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return telefonoInputAdapterRest.editarTelefono(numero, request);
	}

	/**
	 * Elimina un teléfono.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param numero número del teléfono
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si el teléfono no existe (HTTP 404)
	 */
	@Operation(summary = "Elimina un telefono")
	@DeleteMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarTelefono(@PathVariable("database") String database, @PathVariable("numero") String numero)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.eliminarTelefono(database, numero);
	}
}

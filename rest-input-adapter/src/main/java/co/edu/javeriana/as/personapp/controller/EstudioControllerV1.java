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

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST, versión 1, en {@code /api/v1/estudio}.
 *
 * Recibe la petición HTTP y la delega en {@link EstudioInputAdapterRest}. Las excepciones
 * de negocio las convierte en códigos HTTP
 * {@link co.edu.javeriana.as.personapp.exception.GlobalExceptionHandler}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estudio")
@Tag(name = "Estudio", description = "CRUD de estudios (persona + profesion) en MariaDB o MongoDB")
public class EstudioControllerV1 {

	/** Adaptador de entrada que ejecuta las operaciones. */
	private final EstudioInputAdapterRest estudioInputAdapterRest;

	/**
	 * Lista los estudios de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return estudios del motor
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Lista todos los estudios")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		return estudioInputAdapterRest.historial(database);
	}

	/**
	 * Cuenta los estudios de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return número de estudios
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Cuenta los estudios")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return estudioInputAdapterRest.contarEstudios(database);
	}

	/**
	 * Busca un estudio por la cédula de la persona y el identificador de la profesión.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param ccPersona cédula de la persona
	 * @param idProfesion identificador de la profesión
	 * @return el estudio
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si el estudio no existe (HTTP 404)
	 */
	@Operation(summary = "Busca un estudio por su llave compuesta (cedula, id de profesion)")
	@GetMapping(path = "/{database}/{ccPersona}/{idProfesion}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse estudio(@PathVariable("database") String database,
			@PathVariable("ccPersona") Integer ccPersona, @PathVariable("idProfesion") Integer idProfesion)
			throws InvalidOptionException, NoExistException {
		return estudioInputAdapterRest.buscarEstudio(database, ccPersona, idProfesion);
	}

	/**
	 * Crea un estudio. El motor va en el campo {@code database} del cuerpo.
	 *
	 * @param request datos del estudio
	 * @return el estudio creado (HTTP 201)
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws AlreadyExistException si el estudio ya existe (HTTP 409)
	 * @throws NoExistException si la persona o la profesión no existen (HTTP 404)
	 */
	@Operation(summary = "Crea un estudio (persona y profesion deben existir en el mismo motor)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse crearEstudio(@RequestBody EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		return estudioInputAdapterRest.crearEstudio(request);
	}

	/**
	 * Edita la fecha de grado y la universidad de un estudio.
	 *
	 * @param ccPersona cédula de la persona
	 * @param idProfesion identificador de la profesión
	 * @param request datos nuevos y motor
	 * @return el estudio actualizado
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws NoExistException si el estudio no existe (HTTP 404)
	 */
	@Operation(summary = "Edita fecha de grado y universidad de un estudio")
	@PutMapping(path = "/{ccPersona}/{idProfesion}", produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse editarEstudio(@PathVariable("ccPersona") Integer ccPersona,
			@PathVariable("idProfesion") Integer idProfesion, @RequestBody EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return estudioInputAdapterRest.editarEstudio(ccPersona, idProfesion, request);
	}

	/**
	 * Elimina un estudio.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param ccPersona cédula de la persona
	 * @param idProfesion identificador de la profesión
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si el estudio no existe (HTTP 404)
	 */
	@Operation(summary = "Elimina un estudio")
	@DeleteMapping(path = "/{database}/{ccPersona}/{idProfesion}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarEstudio(@PathVariable("database") String database,
			@PathVariable("ccPersona") Integer ccPersona, @PathVariable("idProfesion") Integer idProfesion)
			throws InvalidOptionException, NoExistException {
		return estudioInputAdapterRest.eliminarEstudio(database, ccPersona, idProfesion);
	}
}

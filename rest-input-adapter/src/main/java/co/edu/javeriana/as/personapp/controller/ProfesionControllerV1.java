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

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST, versión 1, en {@code /api/v1/profesion}.
 *
 * Recibe la petición HTTP y la delega en {@link ProfesionInputAdapterRest}. Las excepciones
 * de negocio las convierte en códigos HTTP
 * {@link co.edu.javeriana.as.personapp.exception.GlobalExceptionHandler}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profesion")
@Tag(name = "Profesion", description = "CRUD de profesiones en MariaDB o MongoDB")
public class ProfesionControllerV1 {

	/** Adaptador de entrada que ejecuta las operaciones. */
	private final ProfesionInputAdapterRest profesionInputAdapterRest;

	/**
	 * Lista las profesiones de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return profesiones del motor
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Lista todas las profesiones")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProfesionResponse> profesiones(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		return profesionInputAdapterRest.historial(database);
	}

	/**
	 * Cuenta las profesiones de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return número de profesiones
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Cuenta las profesiones")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return profesionInputAdapterRest.contarProfesiones(database);
	}

	/**
	 * Busca una profesión por identificador.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return la profesión
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la profesión no existe (HTTP 404)
	 */
	@Operation(summary = "Busca una profesion por id")
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse profesion(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.buscarProfesion(database, id);
	}

	/**
	 * Lista los estudios registrados en una profesión, con el nombre de cada persona.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return estudios de la profesión
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la profesión no existe (HTTP 404)
	 */
	@Operation(summary = "Lista los estudios (personas) de una profesion")
	@GetMapping(path = "/{database}/{id}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.estudiosDeProfesion(database, id);
	}

	/**
	 * Crea una profesión. El motor va en el campo {@code database} del cuerpo.
	 *
	 * @param request datos de la profesión
	 * @return la profesión creada (HTTP 201)
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws AlreadyExistException si el identificador ya existe (HTTP 409)
	 */
	@Operation(summary = "Crea una profesion")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		return profesionInputAdapterRest.crearProfesion(request);
	}

	/**
	 * Edita el nombre y la descripción de una profesión.
	 *
	 * @param id identificador de la profesión
	 * @param request datos nuevos y motor
	 * @return la profesión actualizada
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws NoExistException si la profesión no existe (HTTP 404)
	 */
	@Operation(summary = "Edita una profesion")
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse editarProfesion(@PathVariable("id") Integer id, @RequestBody ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return profesionInputAdapterRest.editarProfesion(id, request);
	}

	/**
	 * Elimina una profesión junto con sus estudios.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param id identificador de la profesión
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la profesión no existe (HTTP 404)
	 */
	@Operation(summary = "Elimina una profesion junto con sus estudios")
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarProfesion(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.eliminarProfesion(database, id);
	}
}

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

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST, versión 1, en {@code /api/v1/persona}.
 *
 * Recibe la petición HTTP y la delega en {@link PersonaInputAdapterRest}. Las excepciones
 * de negocio las convierte en códigos HTTP
 * {@link co.edu.javeriana.as.personapp.exception.GlobalExceptionHandler}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/persona")
@Tag(name = "Persona", description = "CRUD de personas en MariaDB o MongoDB")
public class PersonaControllerV1 {

	/** Adaptador de entrada que ejecuta las operaciones. */
	private final PersonaInputAdapterRest personaInputAdapterRest;

	/**
	 * Lista las personas de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return personas del motor
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Lista todas las personas")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database.toUpperCase());
	}

	/**
	 * Cuenta las personas de un motor.
	 *
	 * @param database motor: MARIA o MONGO
	 * @return número de personas
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 */
	@Operation(summary = "Cuenta las personas")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return personaInputAdapterRest.contarPersonas(database);
	}

	/**
	 * Busca una persona por cédula.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return la persona
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la persona no existe (HTTP 404)
	 */
	@Operation(summary = "Busca una persona por cedula")
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse persona(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	/**
	 * Lista los teléfonos de una persona.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return teléfonos de la persona
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la persona no existe (HTTP 404)
	 */
	@Operation(summary = "Lista los telefonos de una persona")
	@GetMapping(path = "/{database}/{cc}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.telefonosDePersona(database, cc);
	}

	/**
	 * Lista los estudios de una persona.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return estudios de la persona
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la persona no existe (HTTP 404)
	 */
	@Operation(summary = "Lista los estudios de una persona")
	@GetMapping(path = "/{database}/{cc}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.estudiosDePersona(database, cc);
	}

	/**
	 * Crea una persona. El motor va en el campo {@code database} del cuerpo.
	 *
	 * @param request datos de la persona
	 * @return la persona creada (HTTP 201)
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws AlreadyExistException si la cédula ya existe (HTTP 409)
	 */
	@Operation(summary = "Crea una persona (el motor va en el campo database del cuerpo)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}

	/**
	 * Edita una persona. La cédula de la ruta prevalece sobre la del cuerpo.
	 *
	 * @param cc cédula de la persona
	 * @param request datos nuevos y motor
	 * @return la persona actualizada
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws InvalidDataException si los datos no son válidos (HTTP 400)
	 * @throws NoExistException si la persona no existe (HTTP 404)
	 */
	@Operation(summary = "Edita una persona (la cedula de la ruta manda sobre la del cuerpo)")
	@PutMapping(path = "/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@PathVariable("cc") Integer cc, @RequestBody PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return personaInputAdapterRest.editarPersona(cc, request);
	}

	/**
	 * Elimina una persona junto con sus teléfonos y estudios.
	 *
	 * @param database motor: MARIA o MONGO
	 * @param cc cédula de la persona
	 * @return resultado de la operación
	 * @throws InvalidOptionException si el motor no es válido (HTTP 400)
	 * @throws NoExistException si la persona no existe (HTTP 404)
	 */
	@Operation(summary = "Elimina una persona junto con sus telefonos y estudios")
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarPersona(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.eliminarPersona(database, cc);
	}
}

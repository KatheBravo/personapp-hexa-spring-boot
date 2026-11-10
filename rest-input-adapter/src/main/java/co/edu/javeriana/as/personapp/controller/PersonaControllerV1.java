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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/persona")
@Tag(name = "Persona", description = "CRUD de personas en MariaDB o MongoDB")
public class PersonaControllerV1 {

	private final PersonaInputAdapterRest personaInputAdapterRest;

	@Operation(summary = "Lista todas las personas")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database.toUpperCase());
	}

	@Operation(summary = "Cuenta las personas")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return personaInputAdapterRest.contarPersonas(database);
	}

	@Operation(summary = "Busca una persona por cedula")
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse persona(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@Operation(summary = "Lista los telefonos de una persona")
	@GetMapping(path = "/{database}/{cc}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.telefonosDePersona(database, cc);
	}

	@Operation(summary = "Lista los estudios de una persona")
	@GetMapping(path = "/{database}/{cc}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.estudiosDePersona(database, cc);
	}

	@Operation(summary = "Crea una persona (el motor va en el campo database del cuerpo)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}

	@Operation(summary = "Edita una persona (la cedula de la ruta manda sobre la del cuerpo)")
	@PutMapping(path = "/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@PathVariable("cc") Integer cc, @RequestBody PersonaRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return personaInputAdapterRest.editarPersona(cc, request);
	}

	@Operation(summary = "Elimina una persona junto con sus telefonos y estudios")
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarPersona(@PathVariable("database") String database, @PathVariable("cc") Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.eliminarPersona(database, cc);
	}
}

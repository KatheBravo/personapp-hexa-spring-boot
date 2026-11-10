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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profesion")
@Tag(name = "Profesion", description = "CRUD de profesiones en MariaDB o MongoDB")
public class ProfesionControllerV1 {

	private final ProfesionInputAdapterRest profesionInputAdapterRest;

	@Operation(summary = "Lista todas las profesiones")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProfesionResponse> profesiones(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		return profesionInputAdapterRest.historial(database);
	}

	@Operation(summary = "Cuenta las profesiones")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return profesionInputAdapterRest.contarProfesiones(database);
	}

	@Operation(summary = "Busca una profesion por id")
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse profesion(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.buscarProfesion(database, id);
	}

	@Operation(summary = "Lista los estudios (personas) de una profesion")
	@GetMapping(path = "/{database}/{id}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.estudiosDeProfesion(database, id);
	}

	@Operation(summary = "Crea una profesion")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		return profesionInputAdapterRest.crearProfesion(request);
	}

	@Operation(summary = "Edita una profesion")
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse editarProfesion(@PathVariable("id") Integer id, @RequestBody ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return profesionInputAdapterRest.editarProfesion(id, request);
	}

	@Operation(summary = "Elimina una profesion junto con sus estudios")
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarProfesion(@PathVariable("database") String database, @PathVariable("id") Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.eliminarProfesion(database, id);
	}
}

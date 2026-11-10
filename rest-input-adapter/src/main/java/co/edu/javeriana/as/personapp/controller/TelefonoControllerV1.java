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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telefono")
@Tag(name = "Telefono", description = "CRUD de telefonos en MariaDB o MongoDB")
public class TelefonoControllerV1 {

	private final TelefonoInputAdapterRest telefonoInputAdapterRest;

	@Operation(summary = "Lista todos los telefonos")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(
			@Parameter(description = "MARIA o MONGO", example = "MARIA") @PathVariable("database") String database)
			throws InvalidOptionException {
		return telefonoInputAdapterRest.historial(database);
	}

	@Operation(summary = "Cuenta los telefonos")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable("database") String database) throws InvalidOptionException {
		return telefonoInputAdapterRest.contarTelefonos(database);
	}

	@Operation(summary = "Busca un telefono por numero")
	@GetMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse telefono(@PathVariable("database") String database, @PathVariable("numero") String numero)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.buscarTelefono(database, numero);
	}

	@Operation(summary = "Crea un telefono (el duenio debe existir en el mismo motor)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse crearTelefono(@RequestBody TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		return telefonoInputAdapterRest.crearTelefono(request);
	}

	@Operation(summary = "Edita un telefono (operador o duenio)")
	@PutMapping(path = "/{numero}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse editarTelefono(@PathVariable("numero") String numero, @RequestBody TelefonoRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		return telefonoInputAdapterRest.editarTelefono(numero, request);
	}

	@Operation(summary = "Elimina un telefono")
	@DeleteMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarTelefono(@PathVariable("database") String database, @PathVariable("numero") String numero)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.eliminarTelefono(database, numero);
	}
}

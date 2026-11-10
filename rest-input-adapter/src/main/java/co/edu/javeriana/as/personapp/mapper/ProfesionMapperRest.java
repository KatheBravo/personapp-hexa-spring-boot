package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

/**
 * Convierte profesiones entre el dominio y los modelos de la API REST.
 */
@Mapper
public class ProfesionMapperRest {

	/**
	 * Convierte una profesión del dominio en respuesta REST.
	 *
	 * @param profession profesión del dominio
	 * @param database nombre del motor que atendió la operación
	 * @return respuesta con los datos de la profesión
	 */
	public ProfesionResponse fromDomainToAdapterRest(Profession profession, String database) {
		return new ProfesionResponse(
				RestValues.text(profession.getIdentification()),
				profession.getName(),
				profession.getDescription(),
				database,
				"OK");
	}

	/**
	 * Convierte la petición REST en profesión del dominio.
	 *
	 * @param request datos recibidos en el cuerpo
	 * @return profesión del dominio
	 * @throws InvalidDataException si falta el identificador o el nombre, o el identificador
	 *         no es numérico
	 */
	public Profession fromAdapterToDomain(ProfesionRequest request) throws InvalidDataException {
		Profession profession = new Profession();
		profession.setIdentification(RestValues.requiredInteger(request.getId(), "id"));
		profession.setName(RestValues.requiredText(request.getName(), "name"));
		profession.setDescription(RestValues.optionalText(request.getDescription()));
		return profession;
	}
}

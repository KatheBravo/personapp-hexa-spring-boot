package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper
public class ProfesionMapperRest {

	public ProfesionResponse fromDomainToAdapterRest(Profession profession, String database) {
		return new ProfesionResponse(
				RestValues.text(profession.getIdentification()),
				profession.getName(),
				profession.getDescription(),
				database,
				"OK");
	}

	public Profession fromAdapterToDomain(ProfesionRequest request) throws InvalidDataException {
		Profession profession = new Profession();
		profession.setIdentification(RestValues.requiredInteger(request.getId(), "id"));
		profession.setName(RestValues.requiredText(request.getName(), "name"));
		profession.setDescription(RestValues.optionalText(request.getDescription()));
		return profession;
	}
}

package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	private final ProfessionOutputPort professionOutputPortMaria;
	private final ProfessionOutputPort professionOutputPortMongo;
	private final ProfesionMapperRest profesionMapperRest;
	private final EstudioMapperRest estudioMapperRest;

	public ProfesionInputAdapterRest(
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			ProfesionMapperRest profesionMapperRest, EstudioMapperRest estudioMapperRest) {
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.profesionMapperRest = profesionMapperRest;
		this.estudioMapperRest = estudioMapperRest;
	}

	private ProfessionInputPort setProfessionOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA ? new ProfessionUseCase(professionOutputPortMaria)
				: new ProfessionUseCase(professionOutputPortMongo);
	}

	public List<ProfesionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial ProfesionEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setProfessionOutputPortInjection(db).findAll().stream()
				.map(profession -> profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel())).toList();
	}

	public ProfesionResponse buscarProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Profession profession = setProfessionOutputPortInjection(db).findOne(id);
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	public ProfesionResponse crearProfesion(ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Profession profession = setProfessionOutputPortInjection(db)
				.create(profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	public ProfesionResponse editarProfesion(Integer id, ProfesionRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setId(String.valueOf(id));
		Profession profession = setProfessionOutputPortInjection(db).edit(id,
				profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, db.getLabel());
	}

	public Response eliminarProfesion(String database, Integer id) throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setProfessionOutputPortInjection(db).drop(id);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Profession " + id + (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in "
						+ db.getLabel() + " (with its studies)",
				LocalDateTime.now());
	}

	public Integer contarProfesiones(String database) throws InvalidOptionException {
		return setProfessionOutputPortInjection(DatabaseOption.from(database)).count();
	}

	public List<EstudioResponse> estudiosDeProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		return setProfessionOutputPortInjection(db).getStudies(id).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}
}

package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {

	private final StudyOutputPort studyOutputPortMaria;
	private final StudyOutputPort studyOutputPortMongo;
	private final PersonOutputPort personOutputPortMaria;
	private final PersonOutputPort personOutputPortMongo;
	private final ProfessionOutputPort professionOutputPortMaria;
	private final ProfessionOutputPort professionOutputPortMongo;
	private final EstudioMapperRest estudioMapperRest;

	public EstudioInputAdapterRest(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyOutputPortMaria,
			@Qualifier("studyOutputAdapterMongo") StudyOutputPort studyOutputPortMongo,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPortMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personOutputPortMongo,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPortMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionOutputPortMongo,
			EstudioMapperRest estudioMapperRest) {
		this.studyOutputPortMaria = studyOutputPortMaria;
		this.studyOutputPortMongo = studyOutputPortMongo;
		this.personOutputPortMaria = personOutputPortMaria;
		this.personOutputPortMongo = personOutputPortMongo;
		this.professionOutputPortMaria = professionOutputPortMaria;
		this.professionOutputPortMongo = professionOutputPortMongo;
		this.estudioMapperRest = estudioMapperRest;
	}

	/** Los tres puertos (estudios, personas y profesiones) siempre del MISMO motor. */
	private StudyInputPort setStudyOutputPortInjection(DatabaseOption dbOption) {
		return dbOption == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
	}

	public List<EstudioResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial EstudiosEntity in Input Adapter");
		DatabaseOption db = DatabaseOption.from(database);
		return setStudyOutputPortInjection(db).findAll().stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel())).toList();
	}

	public EstudioResponse buscarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Study study = setStudyOutputPortInjection(db).findOne(personId, professionId);
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	public EstudioResponse crearEstudio(EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, AlreadyExistException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		Study study = setStudyOutputPortInjection(db).create(estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	public EstudioResponse editarEstudio(Integer personId, Integer professionId, EstudioRequest request)
			throws InvalidOptionException, InvalidDataException, NoExistException {
		DatabaseOption db = DatabaseOption.from(request.getDatabase());
		request.setPersonId(String.valueOf(personId));
		request.setProfessionId(String.valueOf(professionId));
		Study study = setStudyOutputPortInjection(db).edit(personId, professionId,
				estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, db.getLabel());
	}

	public Response eliminarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption db = DatabaseOption.from(database);
		Boolean deleted = setStudyOutputPortInjection(db).drop(personId, professionId);
		return new Response(Boolean.TRUE.equals(deleted) ? "OK" : "ERROR",
				"Study of person " + personId + " in profession " + professionId
						+ (Boolean.TRUE.equals(deleted) ? " deleted" : " was not deleted") + " in " + db.getLabel(),
				LocalDateTime.now());
	}

	public Integer contarEstudios(String database) throws InvalidOptionException {
		return setStudyOutputPortInjection(DatabaseOption.from(database)).count();
	}
}

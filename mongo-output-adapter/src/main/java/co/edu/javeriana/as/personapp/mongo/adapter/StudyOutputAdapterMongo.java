package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
@RequiredArgsConstructor
public class StudyOutputAdapterMongo implements StudyOutputPort {

	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	private final EstudiosMapperMongo estudiosMapperMongo;

	@Override
	public Study save(Study study) {
		log.debug("Into save study on Adapter MongoDB");
		estudiosRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
		return findById(study.getPerson().getIdentification(), study.getProfession().getIdentification());
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete study on Adapter MongoDB");
		String id = EstudiosMapperMongo.buildId(personIdentification, professionIdentification);
		estudiosRepositoryMongo.deleteById(id);
		return !estudiosRepositoryMongo.existsById(id);
	}

	@Override
	public List<Study> find() {
		log.debug("Into find study on Adapter MongoDB");
		return estudiosRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById study on Adapter MongoDB");
		String id = EstudiosMapperMongo.buildId(personIdentification, professionIdentification);
		return estudiosRepositoryMongo.findById(id).map(estudiosMapperMongo::fromAdapterToDomain).orElse(null);
	}
}

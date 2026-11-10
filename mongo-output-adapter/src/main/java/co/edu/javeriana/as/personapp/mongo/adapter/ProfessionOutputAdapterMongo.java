package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMongo")
@RequiredArgsConstructor
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {

	private final ProfesionRepositoryMongo profesionRepositoryMongo;

	private final EstudiosRepositoryMongo estudiosRepositoryMongo;

	private final ProfesionMapperMongo profesionMapperMongo;

	@Override
	public Profession save(Profession profession) {
		log.debug("Into save profession on Adapter MongoDB");
		profesionRepositoryMongo.save(profesionMapperMongo.fromDomainToAdapter(profession));
		return findById(profession.getIdentification());
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete profession on Adapter MongoDB");
		estudiosRepositoryMongo.eliminarPorProfesion(identification);
		profesionRepositoryMongo.deleteById(identification);
		return !profesionRepositoryMongo.existsById(identification);
	}

	@Override
	public List<Profession> find() {
		log.debug("Into find profession on Adapter MongoDB");
		return profesionRepositoryMongo.findAll().stream().map(profesionMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Profession findById(Integer identification) {
		log.debug("Into findById profession on Adapter MongoDB");
		return profesionRepositoryMongo.findById(identification).map(profesionMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}
}

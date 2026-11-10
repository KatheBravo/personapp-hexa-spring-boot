package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.ProfesionMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class ProfessionOutputAdapterMaria implements ProfessionOutputPort {

	private final ProfesionRepositoryMaria profesionRepositoryMaria;

	private final ProfesionMapperMaria profesionMapperMaria;

	@Override
	public Profession save(Profession profession) {
		log.debug("Into save profession on Adapter MariaDB");
		ProfesionEntity profesionEntity = profesionRepositoryMaria.findById(profession.getIdentification())
				.map(existing -> profesionMapperMaria.copyToAdapter(profession, existing))
				.orElseGet(() -> profesionMapperMaria.fromDomainToAdapter(profession));
		return profesionMapperMaria.fromAdapterToDomain(profesionRepositoryMaria.save(profesionEntity));
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete profession on Adapter MariaDB");
		// cascade = ALL en ProfesionEntity borra tambien los estudios de esa profesion
		profesionRepositoryMaria.deleteById(identification);
		profesionRepositoryMaria.flush();
		return profesionRepositoryMaria.findById(identification).isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Profession> find() {
		log.debug("Into find profession on Adapter MariaDB");
		return profesionRepositoryMaria.findAll().stream().map(profesionMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Profession findById(Integer identification) {
		log.debug("Into findById profession on Adapter MariaDB");
		return profesionRepositoryMaria.findById(identification).map(profesionMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}
}

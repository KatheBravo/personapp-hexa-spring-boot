package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class StudyOutputAdapterMaria implements StudyOutputPort {

	private final EstudiosRepositoryMaria estudiosRepositoryMaria;

	private final PersonaRepositoryMaria personaRepositoryMaria;

	private final ProfesionRepositoryMaria profesionRepositoryMaria;

	private final EstudiosMapperMaria estudiosMapperMaria;

	@Override
	public Study save(Study study) {
		log.debug("Into save study on Adapter MariaDB");
		EstudiosEntity estudiosEntity = estudiosMapperMaria.fromDomainToAdapter(study);
		// Las columnas cc_per e id_prof se escriben por la llave compuesta; las relaciones
		// se llenan con las entidades administradas para devolver el estudio completo
		estudiosEntity.setPersona(personaRepositoryMaria.findById(study.getPerson().getIdentification()).orElseThrow());
		estudiosEntity.setProfesion(
				profesionRepositoryMaria.findById(study.getProfession().getIdentification()).orElseThrow());
		return estudiosMapperMaria.fromAdapterToDomain(estudiosRepositoryMaria.save(estudiosEntity));
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete study on Adapter MariaDB");
		EstudiosEntityPK pk = new EstudiosEntityPK(professionIdentification, personIdentification);
		estudiosRepositoryMaria.findById(pk).ifPresent(estudio -> {
			// Se saca de las listas de persona y profesion para que su cascade no lo vuelva a guardar
			if (estudio.getPersona() != null && estudio.getPersona().getEstudios() != null) {
				estudio.getPersona().getEstudios().remove(estudio);
			}
			if (estudio.getProfesion() != null && estudio.getProfesion().getEstudios() != null) {
				estudio.getProfesion().getEstudios().remove(estudio);
			}
			estudiosRepositoryMaria.delete(estudio);
		});
		estudiosRepositoryMaria.flush();
		return estudiosRepositoryMaria.findById(pk).isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Study> find() {
		log.debug("Into find study on Adapter MariaDB");
		return estudiosRepositoryMaria.findAll().stream().map(estudiosMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById study on Adapter MariaDB");
		// Ojo con el orden: la llave de la tabla es (id_prof, cc_per)
		EstudiosEntityPK pk = new EstudiosEntityPK(professionIdentification, personIdentification);
		return estudiosRepositoryMaria.findById(pk).map(estudiosMapperMaria::fromAdapterToDomain).orElse(null);
	}
}

package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.TelefonoMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("phoneOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class PhoneOutputAdapterMaria implements PhoneOutputPort {

	private final TelefonoRepositoryMaria telefonoRepositoryMaria;

	private final PersonaRepositoryMaria personaRepositoryMaria;

	private final TelefonoMapperMaria telefonoMapperMaria;

	@Override
	public Phone save(Phone phone) {
		log.debug("Into save phone on Adapter MariaDB");
		TelefonoEntity telefonoEntity = telefonoMapperMaria.fromDomainToAdapter(phone);
		// Se usa la persona administrada por JPA (no una copia) como duenio del telefono
		PersonaEntity duenio = personaRepositoryMaria.findById(phone.getOwner().getIdentification()).orElseThrow();
		telefonoEntity.setDuenio(duenio);
		return telefonoMapperMaria.fromAdapterToDomain(telefonoRepositoryMaria.save(telefonoEntity));
	}

	@Override
	public Boolean delete(String number) {
		log.debug("Into delete phone on Adapter MariaDB");
		telefonoRepositoryMaria.findById(number).ifPresent(telefono -> {
			// Se saca de la lista del duenio para que el cascade de PersonaEntity no lo vuelva a guardar
			if (telefono.getDuenio() != null && telefono.getDuenio().getTelefonos() != null) {
				telefono.getDuenio().getTelefonos().remove(telefono);
			}
			telefonoRepositoryMaria.delete(telefono);
		});
		telefonoRepositoryMaria.flush();
		return telefonoRepositoryMaria.findById(number).isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Phone> find() {
		log.debug("Into find phone on Adapter MariaDB");
		return telefonoRepositoryMaria.findAll().stream().map(telefonoMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Phone findById(String number) {
		log.debug("Into findById phone on Adapter MariaDB");
		return telefonoRepositoryMaria.findById(number).map(telefonoMapperMaria::fromAdapterToDomain).orElse(null);
	}
}

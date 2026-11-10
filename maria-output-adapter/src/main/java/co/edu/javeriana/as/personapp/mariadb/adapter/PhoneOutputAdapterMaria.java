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

/**
 * Adaptador de salida de teléfonos para MariaDB. Nombre del bean:
 * {@code phoneOutputAdapterMaria}.
 */
@Slf4j
@Adapter("phoneOutputAdapterMaria")
@Transactional
@RequiredArgsConstructor
public class PhoneOutputAdapterMaria implements PhoneOutputPort {

	/** Repositorio JPA de la tabla telefono. */
	private final TelefonoRepositoryMaria telefonoRepositoryMaria;

	/** Repositorio JPA de la tabla persona, usado para cargar el dueño. */
	private final PersonaRepositoryMaria personaRepositoryMaria;

	/** Conversor entre {@link Phone} y {@link TelefonoEntity}. */
	private final TelefonoMapperMaria telefonoMapperMaria;

	/**
	 * Guarda el teléfono asociado a la entidad administrada de su dueño.
	 *
	 * @param phone teléfono a guardar; el dueño debe existir
	 * @return el teléfono guardado con los datos del dueño
	 * @throws java.util.NoSuchElementException si el dueño no existe en MariaDB
	 */
	@Override
	public Phone save(Phone phone) {
		log.debug("Into save phone on Adapter MariaDB");
		TelefonoEntity telefonoEntity = telefonoMapperMaria.fromDomainToAdapter(phone);
		PersonaEntity duenio = personaRepositoryMaria.findById(phone.getOwner().getIdentification()).orElseThrow();
		telefonoEntity.setDuenio(duenio);
		return telefonoMapperMaria.fromAdapterToDomain(telefonoRepositoryMaria.save(telefonoEntity));
	}

	/**
	 * Elimina el teléfono. Antes lo retira de la lista del dueño para que el
	 * {@code cascade} de la persona no lo vuelva a guardar.
	 *
	 * @param number número del teléfono
	 * @return {@code true} si el teléfono ya no existe
	 */
	@Override
	public Boolean delete(String number) {
		log.debug("Into delete phone on Adapter MariaDB");
		telefonoRepositoryMaria.findById(number).ifPresent(telefono -> {
			if (telefono.getDuenio() != null && telefono.getDuenio().getTelefonos() != null) {
				telefono.getDuenio().getTelefonos().remove(telefono);
			}
			telefonoRepositoryMaria.delete(telefono);
		});
		telefonoRepositoryMaria.flush();
		return telefonoRepositoryMaria.findById(number).isEmpty();
	}

	/**
	 * Consulta todos los teléfonos de MariaDB.
	 *
	 * @return lista de teléfonos con los datos de su dueño
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Phone> find() {
		log.debug("Into find phone on Adapter MariaDB");
		return telefonoRepositoryMaria.findAll().stream().map(telefonoMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca un teléfono por número en MariaDB.
	 *
	 * @param number número del teléfono
	 * @return el teléfono encontrado, o {@code null} si no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public Phone findById(String number) {
		log.debug("Into findById phone on Adapter MariaDB");
		return telefonoRepositoryMaria.findById(number).map(telefonoMapperMaria::fromAdapterToDomain).orElse(null);
	}
}

package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de salida de teléfonos para MongoDB. Nombre del bean:
 * {@code phoneOutputAdapterMongo}.
 */
@Slf4j
@Adapter("phoneOutputAdapterMongo")
@RequiredArgsConstructor
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

	/** Repositorio de la colección telefono. */
	private final TelefonoRepositoryMongo telefonoRepositoryMongo;

	/** Conversor entre {@link Phone} y {@code TelefonoDocument}. */
	private final TelefonoMapperMongo telefonoMapperMongo;

	/**
	 * Guarda el teléfono y lo vuelve a leer para resolver la referencia al dueño con
	 * todos sus datos.
	 *
	 * @param phone teléfono a guardar
	 * @return el teléfono guardado con los datos del dueño
	 */
	@Override
	public Phone save(Phone phone) {
		log.debug("Into save phone on Adapter MongoDB");
		telefonoRepositoryMongo.save(telefonoMapperMongo.fromDomainToAdapter(phone));
		return findById(phone.getNumber());
	}

	/**
	 * Elimina el teléfono.
	 *
	 * @param number número del teléfono
	 * @return {@code true} si el teléfono ya no existe
	 */
	@Override
	public Boolean delete(String number) {
		log.debug("Into delete phone on Adapter MongoDB");
		telefonoRepositoryMongo.deleteById(number);
		return !telefonoRepositoryMongo.existsById(number);
	}

	/**
	 * Consulta todos los teléfonos de MongoDB.
	 *
	 * @return lista de teléfonos con los datos de su dueño
	 */
	@Override
	public List<Phone> find() {
		log.debug("Into find phone on Adapter MongoDB");
		return telefonoRepositoryMongo.findAll().stream().map(telefonoMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/**
	 * Busca un teléfono por número en MongoDB.
	 *
	 * @param number número del teléfono
	 * @return el teléfono encontrado, o {@code null} si no existe
	 */
	@Override
	public Phone findById(String number) {
		log.debug("Into findById phone on Adapter MongoDB");
		return telefonoRepositoryMongo.findById(number).map(telefonoMapperMongo::fromAdapterToDomain).orElse(null);
	}
}

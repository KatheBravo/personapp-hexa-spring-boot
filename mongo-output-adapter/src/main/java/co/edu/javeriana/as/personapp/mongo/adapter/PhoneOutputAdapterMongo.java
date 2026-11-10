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

@Slf4j
@Adapter("phoneOutputAdapterMongo")
@RequiredArgsConstructor
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

	private final TelefonoRepositoryMongo telefonoRepositoryMongo;

	private final TelefonoMapperMongo telefonoMapperMongo;

	@Override
	public Phone save(Phone phone) {
		log.debug("Into save phone on Adapter MongoDB");
		telefonoRepositoryMongo.save(telefonoMapperMongo.fromDomainToAdapter(phone));
		// Se relee para resolver la referencia al duenio con todos sus datos
		return findById(phone.getNumber());
	}

	@Override
	public Boolean delete(String number) {
		log.debug("Into delete phone on Adapter MongoDB");
		telefonoRepositoryMongo.deleteById(number);
		return !telefonoRepositoryMongo.existsById(number);
	}

	@Override
	public List<Phone> find() {
		log.debug("Into find phone on Adapter MongoDB");
		return telefonoRepositoryMongo.findAll().stream().map(telefonoMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Phone findById(String number) {
		log.debug("Into findById phone on Adapter MongoDB");
		return telefonoRepositoryMongo.findById(number).map(telefonoMapperMongo::fromAdapterToDomain).orElse(null);
	}
}

package co.edu.javeriana.as.personapp.mariadb.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

@Mapper
public class TelefonoMapperMaria {

	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		telefonoEntity.setDuenio(validateDuenio(phone.getOwner()));
		return telefonoEntity;
	}

	/**
	 * Del duenio solo importa la llave (cc): es lo que se guarda en la columna
	 * "duenio". El adaptador reemplaza esta referencia por la persona real.
	 */
	private PersonaEntity validateDuenio(Person owner) {
		return owner != null ? new PersonaEntity(owner.getIdentification()) : null;
	}

	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		if (telefonoEntity.getOper() != null) {
			phone.setCompany(telefonoEntity.getOper());
		}
		phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
		return phone;
	}

	/** Duenio "basico" (sin sus telefonos): evita la recursion infinita de la plantilla. */
	private Person validateOwner(PersonaEntity duenio) {
		return duenio != null ? PersonaMapperMaria.toDomainBasic(duenio) : new Person();
	}
}

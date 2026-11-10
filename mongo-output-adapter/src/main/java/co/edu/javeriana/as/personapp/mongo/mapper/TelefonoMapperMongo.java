package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

@Mapper
public class TelefonoMapperMongo {

	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		TelefonoDocument telefonoDocument = new TelefonoDocument();
		telefonoDocument.setId(phone.getNumber());
		telefonoDocument.setOper(phone.getCompany());
		telefonoDocument.setPrimaryDuenio(validateDuenio(phone.getOwner()));
		return telefonoDocument;
	}

	/**
	 * @DocumentReference solo guarda el _id del documento referenciado, por eso
	 * basta con un PersonaDocument que tenga la cedula.
	 */
	private PersonaDocument validateDuenio(Person owner) {
		if (owner == null) {
			return null;
		}
		PersonaDocument duenio = new PersonaDocument();
		duenio.setId(owner.getIdentification());
		return duenio;
	}

	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		Phone phone = new Phone();
		phone.setNumber(telefonoDocument.getId());
		if (telefonoDocument.getOper() != null) {
			phone.setCompany(telefonoDocument.getOper());
		}
		phone.setOwner(validateOwner(telefonoDocument.getPrimaryDuenio()));
		return phone;
	}

	/** Duenio "basico" (sin sus telefonos): evita la recursion infinita de la plantilla. */
	private Person validateOwner(PersonaDocument duenio) {
		return duenio != null ? PersonaMapperMongo.toDomainBasic(duenio) : new Person();
	}
}

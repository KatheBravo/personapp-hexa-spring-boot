package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

/**
 * Convierte teléfonos entre el dominio ({@link Phone}) y el documento de MongoDB
 * ({@link TelefonoDocument}).
 */
@Mapper
public class TelefonoMapperMongo {

	/**
	 * Convierte un teléfono del dominio en documento.
	 *
	 * @param phone teléfono del dominio
	 * @return documento listo para guardar
	 */
	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		TelefonoDocument telefonoDocument = new TelefonoDocument();
		telefonoDocument.setId(phone.getNumber());
		telefonoDocument.setOper(phone.getCompany());
		telefonoDocument.setPrimaryDuenio(validateDuenio(phone.getOwner()));
		return telefonoDocument;
	}

	/**
	 * Crea la referencia al dueño con solo su cédula; la referencia guarda
	 * únicamente el {@code _id} del documento.
	 *
	 * @param owner dueño del dominio
	 * @return documento de persona con solo el identificador, o {@code null} si no hay dueño
	 */
	private PersonaDocument validateDuenio(Person owner) {
		if (owner == null) {
			return null;
		}
		PersonaDocument duenio = new PersonaDocument();
		duenio.setId(owner.getIdentification());
		return duenio;
	}

	/**
	 * Convierte un documento en teléfono del dominio.
	 *
	 * @param telefonoDocument documento leído de MongoDB
	 * @return teléfono del dominio con los datos de su dueño
	 */
	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		Phone phone = new Phone();
		phone.setNumber(telefonoDocument.getId());
		if (telefonoDocument.getOper() != null) {
			phone.setCompany(telefonoDocument.getOper());
		}
		phone.setOwner(validateOwner(telefonoDocument.getPrimaryDuenio()));
		return phone;
	}

	/**
	 * Convierte el dueño sin sus teléfonos para no recorrer la relación en ciclo.
	 *
	 * @param duenio documento del dueño; puede ser nulo
	 * @return persona del dominio
	 */
	private Person validateOwner(PersonaDocument duenio) {
		return duenio != null ? PersonaMapperMongo.toDomainBasic(duenio) : new Person();
	}
}

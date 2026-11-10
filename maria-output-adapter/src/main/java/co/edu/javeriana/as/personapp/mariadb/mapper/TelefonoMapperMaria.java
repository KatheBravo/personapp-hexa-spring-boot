package co.edu.javeriana.as.personapp.mariadb.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

/**
 * Convierte teléfonos entre el dominio ({@link Phone}) y la entidad JPA
 * ({@link TelefonoEntity}).
 */
@Mapper
public class TelefonoMapperMaria {

	/**
	 * Convierte un teléfono del dominio en entidad.
	 *
	 * @param phone teléfono del dominio
	 * @return entidad lista para guardar
	 */
	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		telefonoEntity.setDuenio(validateDuenio(phone.getOwner()));
		return telefonoEntity;
	}

	/**
	 * Crea la referencia al dueño con solo su cédula, que es lo que se guarda en la
	 * columna {@code duenio}. El adaptador la reemplaza por la entidad administrada.
	 *
	 * @param owner dueño del dominio
	 * @return referencia a la persona, o {@code null} si no hay dueño
	 */
	private PersonaEntity validateDuenio(Person owner) {
		return owner != null ? new PersonaEntity(owner.getIdentification()) : null;
	}

	/**
	 * Convierte una entidad en teléfono del dominio.
	 *
	 * @param telefonoEntity entidad leída de MariaDB
	 * @return teléfono del dominio con los datos de su dueño
	 */
	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		if (telefonoEntity.getOper() != null) {
			phone.setCompany(telefonoEntity.getOper());
		}
		phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
		return phone;
	}

	/**
	 * Convierte el dueño sin sus teléfonos para no recorrer la relación en ciclo.
	 *
	 * @param duenio entidad del dueño; puede ser nula
	 * @return persona del dominio
	 */
	private Person validateOwner(PersonaEntity duenio) {
		return duenio != null ? PersonaMapperMaria.toDomainBasic(duenio) : new Person();
	}
}

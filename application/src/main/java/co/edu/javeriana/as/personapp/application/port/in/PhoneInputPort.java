package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;

@Port
public interface PhoneInputPort {

	/**
	 * El telefono necesita dos puertos de salida del MISMO motor: el de
	 * telefonos y el de personas (para validar que el duenio exista).
	 */
	public void setPersistence(PhoneOutputPort phonePersistence, PersonOutputPort personPersistence);

	public Phone create(Phone phone) throws AlreadyExistException, NoExistException, InvalidDataException;

	public Phone edit(String number, Phone phone) throws NoExistException, InvalidDataException;

	public Boolean drop(String number) throws NoExistException;

	public List<Phone> findAll();

	public Phone findOne(String number) throws NoExistException;

	public Integer count();
}

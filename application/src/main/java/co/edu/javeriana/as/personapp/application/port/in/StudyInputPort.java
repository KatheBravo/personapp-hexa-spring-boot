package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface StudyInputPort {

	/**
	 * El estudio relaciona una persona con una profesion, por eso necesita los
	 * tres puertos de salida del MISMO motor de persistencia.
	 */
	public void setPersistence(StudyOutputPort studyPersistence, PersonOutputPort personPersistence,
			ProfessionOutputPort professionPersistence);

	public Study create(Study study) throws AlreadyExistException, NoExistException, InvalidDataException;

	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException, InvalidDataException;

	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	public List<Study> findAll();

	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	public Integer count();
}

package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Un estudio se identifica por la pareja (cedula de la persona, id de la
 * profesion), igual que la llave primaria compuesta de la tabla estudios.
 */
@Port
public interface StudyOutputPort {
	public Study save(Study study);
	public Boolean delete(Integer personIdentification, Integer professionIdentification);
	public List<Study> find();
	public Study findById(Integer personIdentification, Integer professionIdentification);
}

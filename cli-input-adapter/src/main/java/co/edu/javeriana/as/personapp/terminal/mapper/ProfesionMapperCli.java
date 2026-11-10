package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

/**
 * Convierte profesiones entre el dominio y el modelo de consola.
 */
@Mapper
public class ProfesionMapperCli {

	/**
	 * Convierte una profesión del dominio en el modelo de consola.
	 *
	 * @param profession profesión del dominio
	 * @return modelo de consola
	 */
	public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
		return new ProfesionModelCli(profession.getIdentification(), profession.getName(),
				profession.getDescription());
	}

	/**
	 * Convierte los datos leídos de la consola en profesión del dominio.
	 *
	 * @param profesionModelCli datos leídos de la consola
	 * @return profesión del dominio
	 */
	public Profession fromAdapterCliToDomain(ProfesionModelCli profesionModelCli) {
		Profession profession = new Profession();
		profession.setIdentification(profesionModelCli.getId());
		profession.setName(profesionModelCli.getNombre());
		profession.setDescription(profesionModelCli.getDescripcion());
		return profession;
	}
}

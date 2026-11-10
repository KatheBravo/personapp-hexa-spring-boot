package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaModelCli {
	private Integer cc;
	private String nombre;
	private String apellido;
	private String genero;
	private Integer edad;

	@Override
	public String toString() {
		return String.format("Persona[cc=%d, nombre=%s %s, genero=%s, edad=%s]", cc, nombre, apellido, genero,
				edad != null ? edad : "-");
	}
}

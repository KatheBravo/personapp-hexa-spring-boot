package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionModelCli {
	private Integer id;
	private String nombre;
	private String descripcion;

	@Override
	public String toString() {
		return String.format("Profesion[id=%d, nombre=%s, descripcion=%s]", id, nombre,
				descripcion != null && !descripcion.isBlank() ? descripcion : "-");
	}
}

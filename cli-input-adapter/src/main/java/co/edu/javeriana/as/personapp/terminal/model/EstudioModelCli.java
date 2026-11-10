package co.edu.javeriana.as.personapp.terminal.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	private Integer ccPersona;
	private String nombrePersona;
	private Integer idProfesion;
	private String nombreProfesion;
	private LocalDate fechaGrado;
	private String universidad;

	@Override
	public String toString() {
		return String.format("Estudio[persona=%d %s, profesion=%d %s, fecha=%s, universidad=%s]", ccPersona,
				nombrePersona != null ? "(" + nombrePersona + ")" : "", idProfesion,
				nombreProfesion != null ? "(" + nombreProfesion + ")" : "", fechaGrado != null ? fechaGrado : "-",
				universidad != null && !universidad.isBlank() ? universidad : "-");
	}
}

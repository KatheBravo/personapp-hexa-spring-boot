package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoModelCli {
	private String numero;
	private String operador;
	private Integer ccDuenio;
	private String nombreDuenio;

	@Override
	public String toString() {
		return String.format("Telefono[numero=%s, operador=%s, duenio=%d %s]", numero, operador, ccDuenio,
				nombreDuenio != null ? "(" + nombreDuenio + ")" : "");
	}
}

package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo JSON para crear o editar una persona. Los valores llegan como texto y
 * se validan al convertirlos al dominio en
 * {@link co.edu.javeriana.as.personapp.mapper.RestValues}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de una persona")
public class PersonaRequest {
	@Schema(description = "Cedula (llave primaria)", example = "1010101010")
	private String dni;
	@Schema(description = "Nombre", example = "Laura")
	private String firstName;
	@Schema(description = "Apellido", example = "Gomez")
	private String lastName;
	@Schema(description = "Edad (opcional)", example = "25")
	private String age;
	@Schema(description = "Genero: M o F", example = "F")
	private String sex;
	@Schema(description = "Motor de persistencia: MARIA o MONGO", example = "MARIA")
	private String database;
}

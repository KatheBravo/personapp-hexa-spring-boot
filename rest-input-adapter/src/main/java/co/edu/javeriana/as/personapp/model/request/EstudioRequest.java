package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo JSON para crear o editar un estudio. Los valores llegan como texto y
 * se validan al convertirlos al dominio en
 * {@link co.edu.javeriana.as.personapp.mapper.RestValues}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un estudio (persona + profesion)")
public class EstudioRequest {
	@Schema(description = "Cedula de la persona", example = "1010101010")
	private String personId;
	@Schema(description = "Identificador de la profesion", example = "10")
	private String professionId;
	@Schema(description = "Fecha de grado en formato yyyy-MM-dd (opcional)", example = "2024-06-15")
	private String graduationDate;
	@Schema(description = "Universidad (opcional)", example = "Pontificia Universidad Javeriana")
	private String universityName;
	@Schema(description = "Motor de persistencia: MARIA o MONGO", example = "MARIA")
	private String database;
}

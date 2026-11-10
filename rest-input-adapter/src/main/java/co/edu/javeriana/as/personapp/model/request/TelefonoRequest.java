package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo JSON para crear o editar un teléfono. Los valores llegan como texto y
 * se validan al convertirlos al dominio en
 * {@link co.edu.javeriana.as.personapp.mapper.RestValues}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un telefono")
public class TelefonoRequest {
	@Schema(description = "Numero (llave primaria, solo digitos)", example = "3001234567")
	private String number;
	@Schema(description = "Operador", example = "Claro")
	private String company;
	@Schema(description = "Cedula de la persona duenia del telefono", example = "1010101010")
	private String ownerId;
	@Schema(description = "Motor de persistencia: MARIA o MONGO", example = "MARIA")
	private String database;
}

package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo JSON para crear o editar una profesión. Los valores llegan como texto y
 * se validan al convertirlos al dominio en
 * {@link co.edu.javeriana.as.personapp.mapper.RestValues}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de una profesion")
public class ProfesionRequest {
	@Schema(description = "Identificador de la profesion", example = "10")
	private String id;
	@Schema(description = "Nombre", example = "Ingenieria de Sistemas")
	private String name;
	@Schema(description = "Descripcion (opcional)", example = "Disena y construye sistemas de software")
	private String description;
	@Schema(description = "Motor de persistencia: MARIA o MONGO", example = "MARIA")
	private String database;
}

package co.edu.javeriana.as.personapp.model.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Respuesta genérica para operaciones que no devuelven una entidad, como
 * eliminar, y para los errores de la API.
 */
public class Response {

	/** Estado: {@code OK}, {@code ERROR} o el nombre del código HTTP del error. */
	private String status;

	/** Detalle del resultado. */
	private String description;

	/** Fecha de la respuesta, en formato dd/MM/yyyy. */
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDateTime date;

	/**
	 * Crea la respuesta.
	 *
	 * @param status estado de la operación
	 * @param description detalle del resultado
	 * @param date fecha de la respuesta
	 */
	public Response(String status, String description, LocalDateTime date) {
		super();
		this.status = status;
		this.description = description;
		this.date = date;
	}

	/** @return estado de la operación */
	public String getStatus() {
		return status;
	}

	/** @param status estado de la operación */
	public void setStatus(String status) {
		this.status = status;
	}

	/** @return detalle del resultado */
	public String getDescription() {
		return description;
	}

	/** @param description detalle del resultado */
	public void setDescription(String description) {
		this.description = description;
	}

	/** @return fecha de la respuesta */
	public LocalDateTime getDate() {
		return date;
	}

	/** @param date fecha de la respuesta */
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}

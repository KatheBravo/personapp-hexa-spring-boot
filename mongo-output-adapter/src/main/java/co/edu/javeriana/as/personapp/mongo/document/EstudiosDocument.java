package co.edu.javeriana.as.personapp.mongo.document;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Documento de la colección {@code estudios}.
 *
 * El {@code _id} tiene la forma {@code cedula-idProfesion}, por ejemplo
 * {@code 123456789-1}. La persona y la profesión se guardan como referencias que
 * solo almacenan el {@code _id} del documento referenciado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("estudios")
public class EstudiosDocument {
	/** Identificador con la forma {@code cedula-idProfesion}. */
	@Id
	private String id;

	/** Referencia a la persona del estudio. */
	@DocumentReference(lazy = true)
	private PersonaDocument primaryPersona;

	/** Referencia a la profesión estudiada. */
	@DocumentReference(lazy = true)
	private ProfesionDocument primaryProfesion;

	/** Fecha de grado, guardada en UTC; puede ser nula. */
	private LocalDate fecha;

	/** Universidad. */
	private String univer;
}

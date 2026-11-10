package co.edu.javeriana.as.personapp.mongo.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Documento de la colección {@code persona}. El {@code _id} es la cédula.
 *
 * Los teléfonos y estudios no se guardan en este documento: se consultan por
 * referencia inversa ({@code lookup}) en sus propias colecciones y son de solo
 * lectura.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("persona")
public class PersonaDocument {
	/** Cédula de la persona. */
	@Id
	private Integer id;

	/** Nombre. */
	private String nombre;

	/** Apellido. */
	private String apellido;

	/** Género: {@code M} o {@code F}. */
	private String genero;

	/** Edad; puede ser nula. */
	private Integer edad;

	/** Teléfonos cuyo campo {@code primaryDuenio} apunta a esta persona. */
	@DocumentReference(lazy = true, lookup = "{ 'primaryDuenio' : ?#{#self._id} }")
	@ReadOnlyProperty
	private List<TelefonoDocument> telefonos;

	/** Estudios cuyo campo {@code primaryPersona} apunta a esta persona. */
	@DocumentReference(lazy = true, lookup = "{ 'primaryPersona' : ?#{#self._id} }")
	@ReadOnlyProperty
	private List<EstudiosDocument> estudios;
}

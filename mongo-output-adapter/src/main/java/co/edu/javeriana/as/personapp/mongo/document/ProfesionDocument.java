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
 * Documento de la colección {@code profesion}. El {@code _id} es el identificador
 * de la profesión; los estudios se consultan por referencia inversa.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("profesion")
public class ProfesionDocument {
	/** Identificador de la profesión. */
	@Id
	private Integer id;

	/** Nombre. */
	private String nom;

	/** Descripción. */
	private String des;

	/** Estudios cuyo campo {@code primaryProfesion} apunta a esta profesión. */
	@DocumentReference(lazy = true, lookup = "{ 'primaryProfesion' : ?#{#self._id} }")
	@ReadOnlyProperty
	private List<EstudiosDocument> estudios;
}

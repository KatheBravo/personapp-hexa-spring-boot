package co.edu.javeriana.as.personapp.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Documento de la colección {@code telefono}. El {@code _id} es el número del
 * teléfono.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("telefono")
public class TelefonoDocument {
	/** Número del teléfono. */
	@Id
	private String id;

	/** Operador. */
	private String oper;

	/** Referencia a la persona dueña; en la base de datos se guarda solo su cédula. */
	@DocumentReference(lazy = true)
	private PersonaDocument primaryDuenio;
}

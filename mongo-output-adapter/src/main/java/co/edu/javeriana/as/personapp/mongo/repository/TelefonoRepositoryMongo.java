package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

/**
 * Repositorio Spring Data de la colección {@code telefono}; la llave es el número.
 */
public interface TelefonoRepositoryMongo extends MongoRepository<TelefonoDocument, String> {

	/**
	 * Elimina los teléfonos de una persona. El campo {@code primaryDuenio} guarda solo
	 * la cédula del dueño, por eso la consulta compara directamente con ese número.
	 *
	 * @param cc cédula del dueño
	 * @return número de documentos eliminados
	 */
	@Query(value = "{ 'primaryDuenio' : ?0 }", delete = true)
	Long eliminarPorDuenio(Integer cc);
}

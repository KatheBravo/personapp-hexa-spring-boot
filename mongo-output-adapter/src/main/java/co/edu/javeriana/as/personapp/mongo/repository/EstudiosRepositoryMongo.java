package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;

/**
 * Repositorio Spring Data de la colección {@code estudios}.
 *
 * Además del CRUD de {@link MongoRepository}, borra los estudios de una persona
 * o de una profesión, lo que reemplaza el borrado en cascada de MariaDB.
 */
public interface EstudiosRepositoryMongo extends MongoRepository<EstudiosDocument, String> {

	/**
	 * Elimina los estudios de una persona.
	 *
	 * @param cc cédula de la persona
	 * @return número de documentos eliminados
	 */
	@Query(value = "{ 'primaryPersona' : ?0 }", delete = true)
	Long eliminarPorPersona(Integer cc);

	/**
	 * Elimina los estudios de una profesión.
	 *
	 * @param idProfesion identificador de la profesión
	 * @return número de documentos eliminados
	 */
	@Query(value = "{ 'primaryProfesion' : ?0 }", delete = true)
	Long eliminarPorProfesion(Integer idProfesion);
}

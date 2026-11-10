package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

public interface TelefonoRepositoryMongo extends MongoRepository<TelefonoDocument, String> {

	/**
	 * Con @DocumentReference el campo primaryDuenio guarda solo la cedula del
	 * duenio, por eso la consulta compara directamente contra ese numero.
	 */
	@Query(value = "{ 'primaryDuenio' : ?0 }", delete = true)
	Long eliminarPorDuenio(Integer cc);
}

package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;

/**
 * Repositorio Spring Data de la colección {@code persona}; la llave es la cédula.
 */
public interface PersonaRepositoryMongo extends MongoRepository<PersonaDocument, Integer> {

}

package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

/**
 * Repositorio Spring Data de la colección {@code profesion}; la llave es el
 * identificador de la profesión.
 */
public interface ProfesionRepositoryMongo extends MongoRepository<ProfesionDocument, Integer> {

}

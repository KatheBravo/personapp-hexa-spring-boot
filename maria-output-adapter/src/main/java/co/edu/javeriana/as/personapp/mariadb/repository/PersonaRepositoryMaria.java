package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;

/**
 * Repositorio Spring Data JPA de la tabla {@code persona}. Hereda las operaciones
 * CRUD de {@link JpaRepository}; la llave es la cédula.
 */
public interface PersonaRepositoryMaria extends JpaRepository<PersonaEntity, Integer> {

}

package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

/**
 * Repositorio Spring Data JPA de la tabla {@code profesion}. Hereda las operaciones
 * CRUD de {@link JpaRepository}; la llave es el identificador.
 */
public interface ProfesionRepositoryMaria extends JpaRepository<ProfesionEntity, Integer> {

}

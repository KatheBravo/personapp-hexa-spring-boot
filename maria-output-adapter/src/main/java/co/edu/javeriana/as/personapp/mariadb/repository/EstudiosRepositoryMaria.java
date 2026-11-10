package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

/**
 * Repositorio Spring Data JPA de la tabla {@code estudios}. Hereda las operaciones
 * CRUD de {@link JpaRepository}; la llave es {@link EstudiosEntityPK}.
 */
public interface EstudiosRepositoryMaria extends JpaRepository<EstudiosEntity, EstudiosEntityPK> {

}

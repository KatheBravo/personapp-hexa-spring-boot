package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

/**
 * Repositorio Spring Data JPA de la tabla {@code telefono}. Hereda las operaciones
 * CRUD de {@link JpaRepository}; la llave es el número.
 */
public interface TelefonoRepositoryMaria extends JpaRepository<TelefonoEntity, String> {

}

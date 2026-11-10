-- =====================================================================
-- PersonApp - DDL MariaDB
-- Crea el usuario, el esquema persona_db y las 4 tablas del modelo:
-- persona, profesion, telefono y estudios.
-- Ejecutar como root:  mariadb -u root -p -P 3307 -h 127.0.0.1 < persona_ddl_maria.sql
-- OJO: borra el esquema persona_db si ya existe.
-- =====================================================================
FLUSH PRIVILEGES;
--
DROP USER IF EXISTS 'persona_db'@'%';
DROP SCHEMA IF EXISTS `persona_db`;
--
CREATE USER IF NOT EXISTS 'persona_db'@'%' IDENTIFIED BY 'persona_db';
CREATE SCHEMA IF NOT EXISTS `persona_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--
GRANT EXECUTE, TRIGGER, INSERT, UPDATE, DELETE, SELECT ON `persona_db`.* TO 'persona_db'@'%';
FLUSH PRIVILEGES;
--
USE `persona_db`;
--
CREATE TABLE IF NOT EXISTS `persona_db`.`persona` (
 `cc` INT(15) NOT NULL,
 `nombre` VARCHAR(45) NOT NULL,
 `apellido` VARCHAR(45) NOT NULL,
 `genero` ENUM('M', 'F') NOT NULL,
 `edad` INT(3) NULL DEFAULT NULL,
 CONSTRAINT `persona_pk` PRIMARY KEY (`cc`)
) ENGINE = InnoDB;
--
CREATE TABLE IF NOT EXISTS `persona_db`.`profesion` (
 `id` INT(6) NOT NULL,
 `nom` VARCHAR(90) NOT NULL,
 `des` TEXT NULL DEFAULT NULL,
 CONSTRAINT `profesion_pk` PRIMARY KEY (`id`)
) ENGINE = InnoDB;
--
CREATE TABLE IF NOT EXISTS `persona_db`.`telefono` (
 `num` VARCHAR(15) NOT NULL,
 `oper` VARCHAR(45) NOT NULL,
 `duenio` INT(15) NOT NULL,
 CONSTRAINT `telefono_pk` PRIMARY KEY (`num`),
 CONSTRAINT `telefono_persona_fk` FOREIGN KEY (`duenio`) REFERENCES `persona_db`.`persona` (`cc`)
) ENGINE = InnoDB;
--
CREATE TABLE IF NOT EXISTS `persona_db`.`estudios` (
 `id_prof` INT(6) NOT NULL,
 `cc_per` INT(15) NOT NULL,
 `fecha` DATE NULL DEFAULT NULL,
 `univer` VARCHAR(50) NULL DEFAULT NULL,
 CONSTRAINT `estudios_pk` PRIMARY KEY (`id_prof`, `cc_per`),
 CONSTRAINT `estudio_persona_fk` FOREIGN KEY (`cc_per`) REFERENCES `persona_db`.`persona` (`cc`),
 CONSTRAINT `estudio_profesion_fk` FOREIGN KEY (`id_prof`) REFERENCES `persona_db`.`profesion` (`id`)
) ENGINE = InnoDB;
--
COMMIT;
FLUSH PRIVILEGES;

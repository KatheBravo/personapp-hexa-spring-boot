-- =====================================================================
-- PersonApp - DML MariaDB (datos de prueba)
-- Ejecutar despues del DDL:
--   mariadb -u root -p -P 3307 -h 127.0.0.1 < persona_dml_maria.sql
-- El orden importa por las llaves foraneas: personas y profesiones primero.
-- =====================================================================
SET NAMES utf8mb4;
USE `persona_db`;
--
INSERT INTO
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`)
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);
--
INSERT INTO
	`persona_db`.`profesion`(`id`,`nom`,`des`)
VALUES
	(1,'Ingeniería de Sistemas','Diseña, construye y mantiene sistemas de software'),
	(2,'Medicina','Previene, diagnostica y trata enfermedades'),
	(3,'Derecho','Asesora y representa en asuntos legales'),
	(4,'Arquitectura','Diseña espacios y edificaciones'),
	(5,'Contaduría Pública','Lleva la contabilidad y la información financiera');
--
INSERT INTO
	`persona_db`.`telefono`(`num`,`oper`,`duenio`)
VALUES
	('3001234567','Claro',123456789),
	('3109876543','Movistar',123456789),
	('3205551234','Tigo',321654987),
	('3157778899','Claro',963852741),
	('6012345678','ETB',987654321);
--
INSERT INTO
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`)
VALUES
	(1,123456789,'2015-12-01','Pontificia Universidad Javeriana'),
	(4,123456789,'2019-06-15','Universidad Nacional de Colombia'),
	(2,321654987,'2018-11-30','Universidad del Rosario'),
	(5,987654321,'2010-07-20','Universidad de los Andes'),
	(3,963852741,null,'Universidad Externado de Colombia');
--
COMMIT;

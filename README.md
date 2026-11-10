# PersonApp: servicio web con arquitectura hexagonal

[![build](https://github.com/KatheBravo/personapp-hexa-spring-boot/actions/workflows/build.yml/badge.svg)](https://github.com/KatheBravo/personapp-hexa-spring-boot/actions/workflows/build.yml)

Laboratorio 2 de Arquitectura de Software, Pontificia Universidad Javeriana: implementación de un servicio web en arquitectura hexagonal con los patrones *Repository* y *Service*.

| | |
|---|---|
| Autora | Katherine Bravo |
| Versión entregada | [v1.0.1](https://github.com/KatheBravo/personapp-hexa-spring-boot/releases/tag/v1.0.1) |
| Plantilla base | [andres-karoll/personapp-hexa-spring-boot](https://github.com/andres-karoll/personapp-hexa-spring-boot) |

## Contenido de la entrega

| Entregable | Ubicación |
|---|---|
| Código fuente | Este repositorio, tag `v1.0.1` |
| Scripts DDL y DML de MariaDB y MongoDB | Carpeta [`scripts/`](scripts) |
| Configuración del ambiente, compilación y despliegue | Este README |
| Ejecución de las pruebas y evidencias | [GitHub Actions](https://github.com/KatheBravo/personapp-hexa-spring-boot/actions) |

## Descripción

PersonApp administra personas, profesiones, teléfonos y estudios con operaciones CRUD completas. Es un proyecto Maven multimódulo organizado según la arquitectura hexagonal (puertos y adaptadores):

- **Adaptadores de entrada:** una API REST documentada con Swagger (OpenAPI 3) y una aplicación de consola (CLI). Cada uno es una aplicación Spring Boot independiente.
- **Adaptadores de salida:** MariaDB, con Spring Data JPA, y MongoDB, con Spring Data MongoDB.
- **Cambio de motor en tiempo de ejecución:** cada operación indica si usa `MARIA` o `MONGO`, sin recompilar ni reiniciar la aplicación.

## Modelo de datos

| Tabla / colección | Campos |
|---|---|
| `persona` | `cc` INT(15) PK, `nombre` VARCHAR(45), `apellido` VARCHAR(45), `genero` ENUM('M','F'), `edad` INT(3) |
| `profesion` | `id` INT(6) PK, `nom` VARCHAR(90), `des` TEXT |
| `telefono` | `num` VARCHAR(15) PK, `oper` VARCHAR(45), `duenio` INT(15) FK a `persona` |
| `estudios` | `id_prof` INT(6) FK a `profesion`, `cc_per` INT(15) FK a `persona`, `fecha` DATE, `univer` VARCHAR(50); PK (`id_prof`, `cc_per`) |

En MongoDB el `_id` de cada estudio es el texto `cedula-idProfesion` y las relaciones se guardan como referencias al `_id` del documento relacionado.

## Tecnologías

| Componente | Versión | Observación |
|---|---|---|
| Java (JDK) | 21 LTS | |
| Spring Boot | 4.1.2 | Spring Framework 7, Hibernate 7 y Spring Data MongoDB 5 |
| Maven | 3.9 o superior | Proyecto multimódulo |
| MariaDB | 11.8 LTS | Puerto 3307 |
| MongoDB | 7.0 | Puerto 27017. Las versiones 8.x no arrancan en kernels Linux 6.19 a 7.0.13 (incompatibilidad con TCMalloc) |
| springdoc-openapi | 3.1.1 | Swagger UI y especificación OpenAPI 3 |
| Lombok | 1.18.48 | Requiere *annotation processing* en el IDE |
| Docker Compose | Opcional | Levanta las dos bases de datos con un comando |

## Arquitectura

```
personapp-hexa-spring-boot
├── common                 Anotaciones (@Adapter, @Port, @UseCase, @Mapper), excepciones y DatabaseOption
├── domain                 Entidades de negocio y sus reglas: Person, Phone, Profession, Study, Gender
├── application            Puertos de entrada, puertos de salida y casos de uso
├── maria-output-adapter   Adaptador de salida MariaDB: entidades JPA, repositorios y mappers
├── mongo-output-adapter   Adaptador de salida MongoDB: documentos, repositorios y mappers
├── rest-input-adapter     Adaptador de entrada REST: controladores, modelos, Swagger y manejo de errores
├── cli-input-adapter      Adaptador de entrada de consola: menús, modelos y mappers
└── scripts                Scripts DDL y DML, y pruebas de integración
```

Las dependencias van siempre de afuera hacia adentro:

```
rest-input-adapter ─┐                      ┌─ maria-output-adapter
                    ├──► application ◄─────┤
cli-input-adapter  ─┘     │       │        └─ mongo-output-adapter
                          ▼       ▼
                       domain   common
```

- `domain` no depende de ningún otro módulo ni de Spring.
- `application` define los puertos (interfaces) y los casos de uso; solo conoce a `domain` y `common`.
- Los adaptadores de salida implementan los puertos de salida y los de entrada usan los puertos de entrada.
- El orden de los `<module>` en el `pom.xml` raíz es el orden de compilación.
- El dominio usa nombres en inglés (`Person`, `Phone`) y los adaptadores en español (`PersonaEntity`, `TelefonoDocument`).

Todas las clases y métodos están documentados con Javadoc: qué reciben, qué hacen, qué devuelven y qué excepciones lanzan.

## Requisitos

- JDK 21 (`java -version` debe mostrar 21).
- Maven 3.9 o superior (`mvn -version`).
- Docker Desktop, o MariaDB y MongoDB instalados localmente.
- En el IDE, el plugin de Lombok con *annotation processing* habilitado.

## 1. Configuración del ambiente

### Opción A: Docker (recomendada)

Desde la raíz del repositorio:

```bash
docker compose up -d
docker compose ps        # las dos bases deben quedar en estado "healthy"
```

Se crean dos contenedores:

| Contenedor | Puerto | Usuario administrador | Scripts que ejecuta al crearse |
|---|---|---|---|
| `personapp-mariadb` | 3307 | `root` / `root` | `persona_ddl_maria.sql` y `persona_dml_maria.sql` |
| `personapp-mongodb` | 27017 | `root` / `root` | `persona_ddl_mongo.js` y `persona_dml_mongo.js` |

Los scripts crean la base `persona_db`, el usuario de la aplicación `persona_db` / `persona_db` y los datos de prueba: 5 personas, 5 profesiones, 5 teléfonos y 5 estudios, iguales en ambos motores.

Los scripts se ejecutan solo la primera vez. Para recargar los datos desde cero:

```bash
docker compose down -v
docker compose up -d
```

### Opción B: instalación manual

1. MariaDB en el puerto **3307**, como administrador:
   ```bash
   mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_ddl_maria.sql
   mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_dml_maria.sql
   ```
2. MongoDB en el puerto **27017**:
   ```bash
   mongosh "mongodb://localhost:27017" scripts/persona_ddl_mongo.js
   mongosh "mongodb://localhost:27017" scripts/persona_dml_mongo.js
   ```
   Si MongoDB tiene autenticación activa, agregue `-u <usuario> -p <clave> --authenticationDatabase admin`.

Los scripts DDL eliminan la base `persona_db` si ya existe.

### Configuración de la aplicación

Cada adaptador de entrada tiene su archivo `src/main/resources/application.properties`:

| Propiedad | Valor por defecto |
|---|---|
| `spring.datasource.url` | `jdbc:mariadb://localhost:3307/persona_db` |
| `spring.datasource.username` / `password` | `persona_db` / `persona_db` |
| `spring.mongodb.host` / `port` | `localhost` / `27017` |
| `spring.mongodb.database` | `persona_db` |
| `spring.mongodb.username` / `password` | `persona_db` / `persona_db` |
| `spring.mongodb.authentication-database` | `admin` |
| `server.port` (solo REST) | `3000` |

En Spring Boot 4 las propiedades de MongoDB usan el prefijo `spring.mongodb.*`; el prefijo anterior, `spring.data.mongodb.*`, se ignora.

Cualquier propiedad se puede sobrescribir sin modificar el código:

```bash
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar --server.port=8080
SPRING_DATASOURCE_URL=jdbc:mariadb://otro-servidor:3306/persona_db java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar
```

## 2. Compilación

Desde la raíz:

```bash
mvn clean install
```

Compila los 7 módulos en orden, ejecuta las pruebas unitarias y genera los JAR ejecutables:

- `rest-input-adapter/target/rest-input-adapter-1.0.0.jar`
- `cli-input-adapter/target/cli-input-adapter-1.0.0.jar`

Para compilar sin ejecutar las pruebas: `mvn clean install -DskipTests`.

## 3. Despliegue y ejecución

Las bases de datos deben estar en ejecución (paso 1).

### API REST

```bash
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar
```

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:3000/swagger-ui.html |
| Especificación OpenAPI (JSON) | http://localhost:3000/api-docs |
| Registro | `logs/persona-rest.log` |

### Consola

```bash
java -jar cli-input-adapter/target/cli-input-adapter-1.0.0.jar
```

El menú principal ofrece los módulos 1 Personas, 2 Profesiones, 3 Teléfonos y 4 Estudios. En cada módulo se elige el motor (1 MariaDB, 2 MongoDB) y después la operación: ver todo, buscar, crear, editar, eliminar o contar. El registro queda en `logs/persona-cli.log`.

### Desde el IDE

Ejecutar la clase `PersonAppRestApi` del módulo `rest-input-adapter` o `PersonAppCli` del módulo `cli-input-adapter`.

## API REST

`{database}` es `MARIA` o `MONGO` (también se aceptan `MariaDB` y `MongoDB`, sin importar mayúsculas). En `POST` y `PUT` el motor va en el campo `database` del cuerpo JSON.

| Recurso | Método | Ruta | Descripción |
|---|---|---|---|
| Persona | GET | `/api/v1/persona/{database}` | Listar |
| | GET | `/api/v1/persona/{database}/count` | Contar |
| | GET | `/api/v1/persona/{database}/{cc}` | Buscar por cédula |
| | GET | `/api/v1/persona/{database}/{cc}/telefonos` | Teléfonos de la persona |
| | GET | `/api/v1/persona/{database}/{cc}/estudios` | Estudios de la persona |
| | POST | `/api/v1/persona` | Crear |
| | PUT | `/api/v1/persona/{cc}` | Editar |
| | DELETE | `/api/v1/persona/{database}/{cc}` | Eliminar, con sus teléfonos y estudios |
| Profesión | GET | `/api/v1/profesion/{database}` | Listar |
| | GET | `/api/v1/profesion/{database}/count` | Contar |
| | GET | `/api/v1/profesion/{database}/{id}` | Buscar |
| | GET | `/api/v1/profesion/{database}/{id}/estudios` | Estudios registrados en la profesión |
| | POST | `/api/v1/profesion` | Crear |
| | PUT | `/api/v1/profesion/{id}` | Editar |
| | DELETE | `/api/v1/profesion/{database}/{id}` | Eliminar, con sus estudios |
| Teléfono | GET | `/api/v1/telefono/{database}` | Listar |
| | GET | `/api/v1/telefono/{database}/count` | Contar |
| | GET | `/api/v1/telefono/{database}/{numero}` | Buscar |
| | POST | `/api/v1/telefono` | Crear; el dueño debe existir |
| | PUT | `/api/v1/telefono/{numero}` | Editar operador o dueño |
| | DELETE | `/api/v1/telefono/{database}/{numero}` | Eliminar |
| Estudio | GET | `/api/v1/estudio/{database}` | Listar |
| | GET | `/api/v1/estudio/{database}/count` | Contar |
| | GET | `/api/v1/estudio/{database}/{ccPersona}/{idProfesion}` | Buscar por llave compuesta |
| | POST | `/api/v1/estudio` | Crear; la persona y la profesión deben existir |
| | PUT | `/api/v1/estudio/{ccPersona}/{idProfesion}` | Editar fecha de grado y universidad |
| | DELETE | `/api/v1/estudio/{database}/{ccPersona}/{idProfesion}` | Eliminar |

| Código | Significado |
|---|---|
| 200 | Operación correcta |
| 201 | Registro creado |
| 400 | Datos inválidos o motor no válido |
| 404 | El registro, o uno relacionado, no existe |
| 409 | El registro ya existe |
| 500 | Error inesperado, por ejemplo una base de datos apagada |

Los errores responden con el cuerpo `{ "status", "description", "date" }`.

### Ejemplos

```bash
# Crear la misma persona en MariaDB y en MongoDB, sin reiniciar la API
curl -X POST http://localhost:3000/api/v1/persona -H "Content-Type: application/json" \
  -d '{"dni":"1010101010","firstName":"Laura","lastName":"Gomez","age":"25","sex":"F","database":"MARIA"}'
curl -X POST http://localhost:3000/api/v1/persona -H "Content-Type: application/json" \
  -d '{"dni":"1010101010","firstName":"Laura","lastName":"Gomez","age":"25","sex":"F","database":"MONGO"}'

# Profesión, teléfono y estudio
curl -X POST http://localhost:3000/api/v1/profesion -H "Content-Type: application/json" \
  -d '{"id":"10","name":"Psicologia","description":"Estudia la conducta humana","database":"MARIA"}'
curl -X POST http://localhost:3000/api/v1/telefono -H "Content-Type: application/json" \
  -d '{"number":"3001112233","company":"Claro","ownerId":"1010101010","database":"MARIA"}'
curl -X POST http://localhost:3000/api/v1/estudio -H "Content-Type: application/json" \
  -d '{"personId":"1010101010","professionId":"10","graduationDate":"2024-06-15","universityName":"Pontificia Universidad Javeriana","database":"MARIA"}'

# Consultas
curl http://localhost:3000/api/v1/persona/MARIA/1010101010/telefonos
curl http://localhost:3000/api/v1/estudio/MONGO

# Editar y eliminar
curl -X PUT http://localhost:3000/api/v1/telefono/3001112233 -H "Content-Type: application/json" \
  -d '{"company":"Movistar","ownerId":"1010101010","database":"MARIA"}'
curl -X DELETE http://localhost:3000/api/v1/estudio/MARIA/1010101010/10
```

## Cambio de base de datos en tiempo de ejecución

Cada puerto de salida tiene dos implementaciones registradas como beans con nombre, por ejemplo `personOutputAdapterMaria` y `personOutputAdapterMongo`. Los adaptadores de entrada reciben ambas por constructor con `@Qualifier` y, en cada operación, crean el caso de uso con la del motor solicitado:

```java
private PersonInputPort setPersonOutputPortInjection(DatabaseOption dbOption) {
    return dbOption == DatabaseOption.MARIA ? new PersonUseCase(personOutputPortMaria)
                                            : new PersonUseCase(personOutputPortMongo);
}
```

El caso de uso es una variable local y no un atributo del bean, de modo que dos peticiones simultáneas con motores distintos no comparten estado. Elegir el motor en tiempo de compilación implicaría recompilar o reiniciar con otra configuración; aquí basta con un parámetro de la petición. Este mecanismo es la base de patrones como CQRS, en el que se escribe en un motor y se lee de otro.

## Pruebas

### Pruebas unitarias

```bash
mvn test
```

Las pruebas de `domain` y `application` usan adaptadores de salida en memoria que implementan los puertos. No necesitan MariaDB, MongoDB ni Spring.

### Pruebas de integración

Con las bases de datos en ejecución y el proyecto compilado:

```bash
# API REST: CRUD completo en MariaDB y después en MongoDB, sin reiniciar la API
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar &
bash scripts/prueba-integracion.sh http://localhost:3000

# Consola: alimenta el menú con una secuencia de opciones y verifica la salida
bash scripts/prueba-cli.sh cli-input-adapter/target/cli-input-adapter-1.0.0.jar
```

`prueba-integracion.sh` requiere `curl` y `jq`. Verifica en ambos motores la lectura de los datos de los scripts DML; la creación, edición y eliminación de las 4 entidades; el borrado en cascada; los códigos 400, 404 y 409; las fechas sin desfase por zona horaria, y que Swagger publique las 23 rutas. Al terminar deja las bases como estaban.

### Integración continua

En cada *push*, [GitHub Actions](.github/workflows/build.yml) compila con JDK 21, ejecuta las pruebas unitarias, levanta MariaDB y MongoDB con `docker compose` (lo que ejecuta los scripts DDL y DML) y corre las dos pruebas de integración. El resumen de cada ejecución muestra el resultado de cada caso y el artefacto **evidencias** guarda capturas de Swagger UI, la sesión de la consola y los registros.

| Prueba | Resultado en la versión entregada |
|---|---|
| Pruebas unitarias | 19 de 19 correctas |
| Integración REST, MariaDB y MongoDB | 87 de 87 correctas |
| Consola (CLI) | 11 de 11 verificaciones correctas |

## Trabajo realizado sobre la plantilla

### Corrección de errores

1. `EstudiosMapperMaria.fromAdapterToDomain` y `EstudiosMapperMongo.fromAdapterToDomain` retornaban `null`, por lo que fallaba toda persona con estudios.
2. Los mappers de persona, teléfono y estudio se llamaban entre sí sin fin (`StackOverflowError`). Se agregó una conversión sin listas, `toDomainBasic`, para las referencias.
3. `PersonaMapperRest.fromAdapterToDomain` devolvía una persona vacía, por lo que crear personas por REST no guardaba los datos enviados.
4. `persona_dml_mongo.js` cargaba los datos en `prueba_db` en lugar de `persona_db`, y los scripts `.js` usaban `use`, que `mongosh` no acepta en archivos.
5. Las dependencias circulares entre mappers obligaban a usar `spring.main.allow-circular-references=true`; al eliminarlas, la propiedad dejó de ser necesaria.
6. La comparación `genero == 'F'` con un `Character` nulo producía `NullPointerException`.

### Actualización de versiones (Java 11 y Spring Boot 2.7 a Java 21 y Spring Boot 4.1)

- `javax.persistence.*` pasó a `jakarta.persistence.*` y `javax.transaction.Transactional` al `@Transactional` de Spring.
- `@Temporal` con `java.util.Date` se reemplazó por `LocalDate`, y `@Lob` sobre TEXT por `@Column(columnDefinition = "TEXT")`.
- `springdoc-openapi-ui 1.x` pasó a `springdoc-openapi-starter-webmvc-ui 3.x`, y `spring-boot-starter-web` a `spring-boot-starter-webmvc`.
- Las propiedades `spring.data.mongodb.*` pasaron a `spring.mongodb.*`.
- Las versiones se centralizaron con el BOM `spring-boot-dependencies`; Lombok se declaró como procesador de anotaciones, se habilitó `-parameters` y se configuró `spring-boot-maven-plugin` para generar JAR ejecutables.

### Funcionalidad nueva

- Puertos, casos de uso y adaptadores de MariaDB, MongoDB, REST y consola para profesión, teléfono y estudio.
- Validaciones de negocio en el dominio y en los casos de uso, iguales para ambos motores.
- Manejo global de errores HTTP y documentación de la API con Swagger.
- Pruebas unitarias, pruebas de integración, `docker-compose.yml` e integración continua.

## Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| El IDE no reconoce `getX()` ni `setX()` | Lombok no está configurado | Instalar el plugin de Lombok y habilitar *annotation processing* |
| `Access denied for user 'persona_db'` | No se ejecutó el DDL de MariaDB | Ejecutar `persona_ddl_maria.sql` como administrador |
| `AuthenticationFailed` en MongoDB | No existe el usuario `persona_db` en `admin` | Ejecutar `persona_ddl_mongo.js` |
| `Connection refused` en `localhost:3307` | MariaDB no escucha en el puerto 3307 | Revisar el contenedor con `docker compose ps` o el puerto de la instalación |
| El contenedor de MongoDB termina al iniciar | Imagen 8.x en un kernel Linux 6.19 a 7.0.13 | Usar la imagen `mongo:7.0`, como en `docker-compose.yml` |
| `Port 3000 was already in use` | Otro proceso usa el puerto | Ejecutar con `--server.port=8080` |
| No aparecen los datos de prueba | Los volúmenes de Docker ya existían | `docker compose down -v` y `docker compose up -d` |
| `release version 21 not supported` | Maven usa un JDK anterior | Apuntar `JAVA_HOME` al JDK 21 |

## Licencia

Apache License 2.0, la misma de la plantilla base.

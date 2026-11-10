# PersonApp — Arquitectura Hexagonal con Spring Boot

[![build](https://github.com/KatheBravo/personapp-hexa-spring-boot/actions/workflows/build.yml/badge.svg)](https://github.com/KatheBravo/personapp-hexa-spring-boot/actions/workflows/build.yml)

Laboratorio 2 de Arquitectura de Software — Pontificia Universidad Javeriana.

Servicio web con **arquitectura hexagonal (puertos y adaptadores) + arquitectura limpia**, construido como un proyecto Maven multimódulo. Implementa el CRUD completo de **personas, profesiones, teléfonos y estudios** con:

- **Dos adaptadores de entrada**: API **REST** (documentada con **Swagger / OpenAPI 3**) y **CLI** (consola).
- **Dos adaptadores de salida**: **MariaDB** (JPA / Hibernate, patrón *Repository*) y **MongoDB** (Spring Data MongoDB).
- **Cambio de base de datos en tiempo de ejecución** (en caliente): cada operación indica si usa `MARIA` o `MONGO`, sin recompilar ni reiniciar.

Implementado por **Katherine Bravo** sobre la plantilla del laboratorio de Andrés Sánchez (`andres-karoll/personapp-hexa-spring-boot`).

---

## Stack y versiones

| Componente | Versión | Nota |
|---|---|---|
| Java (JDK) | **21 LTS** | La plantilla venía en JDK 11; se actualizó a la LTS que pide Spring Boot 4 |
| Spring Boot | **4.1.2** | Spring Framework 7, Hibernate 7, Spring Data MongoDB 5 (versiones gestionadas por el BOM) |
| Maven | 3.9 o superior | Proyecto multimódulo con `${revision}` |
| MariaDB | 11.8 LTS | Puerto **3307** |
| MongoDB | 8.0 | Puerto **27017** |
| springdoc-openapi | 3.1.1 | Swagger UI + OpenAPI 3 (línea compatible con Spring Boot 4) |
| Lombok | 1.18.48 | Hay que habilitarlo en el IDE |
| Docker / Docker Compose | Opcional | Para levantar las dos bases de datos con un comando |

---

## Arquitectura

```
personapp-hexa-spring-boot
├── common                 Anotaciones (@Adapter, @Port, @UseCase, @Mapper), excepciones y DatabaseOption
├── domain                 Negocio puro: Person, Phone, Profession, Study, Gender. Sin dependencias
├── application            Puertos de entrada (port/in), puertos de salida (port/out) y casos de uso
├── maria-output-adapter   Adaptador de salida MariaDB: entidades JPA (@Entity), repositorios y mappers
├── mongo-output-adapter   Adaptador de salida MongoDB: documentos (@Document), repositorios y mappers
├── rest-input-adapter     Adaptador de entrada REST: controladores, DTOs, Swagger. Tiene su SpringApplication
├── cli-input-adapter      Adaptador de entrada CLI: menús de consola. Tiene su propia SpringApplication
└── scripts                DDL y DML de MariaDB y MongoDB
```

**Regla de dependencias** (de afuera hacia adentro, nunca al revés):

```
rest-input-adapter ─┐                      ┌─ maria-output-adapter
                    ├──► application ◄─────┤
cli-input-adapter  ─┘     │       │        └─ mongo-output-adapter
                          ▼       ▼
                       domain   common
```

- `domain` no depende de nadie (ni siquiera de Spring).
- `application` solo conoce al dominio y a `common`; define **interfaces** (puertos).
- Los adaptadores **implementan** los puertos de salida o **usan** los puertos de entrada.
- El orden de los `<module>` en el `pom.xml` raíz es el orden de compilación.
- Convención visual: el dominio está en inglés (`Person`, `Phone`…) y los adaptadores en español (`PersonaEntity`, `TelefonoDocument`…).

---

## Requisitos

- **JDK 21** (`java -version` debe mostrar 21).
- **Maven 3.9+** (`mvn -version`).
- **Docker Desktop** (recomendado) **o** MariaDB y MongoDB instalados localmente.
- En el IDE: plugin de **Lombok** instalado y *annotation processing* habilitado.

---

## 1. Configurar el ambiente (bases de datos)

### Opción A — Docker (recomendada)

Desde la raíz del repositorio:

```bash
docker compose up -d
```

Esto crea:

- `personapp-mariadb` en `localhost:3307` (root / `root`), y ejecuta `persona_ddl_maria.sql` y `persona_dml_maria.sql`.
- `personapp-mongodb` en `localhost:27017` (root / `root`), y ejecuta `persona_ddl_mongo.js` y `persona_dml_mongo.js`.

Ambos scripts crean el usuario de la aplicación **`persona_db` / `persona_db`** y la base **`persona_db`**.

Verificar que estén sanos: `docker compose ps` (columna STATUS = `healthy`).

> Los scripts de inicialización solo corren la **primera vez**. Para volver a cargar los datos desde cero: `docker compose down -v` y luego `docker compose up -d`.

### Opción B — Instalación manual

1. Instalar **MariaDB** escuchando en el puerto **3307** y ejecutar como root:
   ```bash
   mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_ddl_maria.sql
   mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_dml_maria.sql
   ```
2. Instalar **MongoDB** en el puerto **27017** y ejecutar:
   ```bash
   mongosh "mongodb://localhost:27017" scripts/persona_ddl_mongo.js
   mongosh "mongodb://localhost:27017" scripts/persona_dml_mongo.js
   ```
   Si MongoDB tiene autenticación activa, agregue `-u <admin> -p <clave> --authenticationDatabase admin`.

> ⚠️ El DDL de cada motor **borra** la base `persona_db` si ya existe.

### Configuración de la aplicación

Cada adaptador de entrada tiene su `src/main/resources/application.properties` con la conexión a ambas bases:

| Propiedad | Valor por defecto |
|---|---|
| `spring.datasource.url` | `jdbc:mariadb://localhost:3307/persona_db` |
| `spring.datasource.username` / `password` | `persona_db` / `persona_db` |
| `spring.mongodb.host` / `port` | `localhost` / `27017` |
| `spring.mongodb.database` | `persona_db` |
| `spring.mongodb.username` / `password` | `persona_db` / `persona_db` |
| `spring.mongodb.authentication-database` | `admin` |
| `server.port` (solo REST) | `3000` |

> En **Spring Boot 4** las propiedades de MongoDB cambiaron de `spring.data.mongodb.*` a `spring.mongodb.*`. Con el prefijo viejo Spring las ignora y se conecta con los valores por defecto (sin usuario).

Cualquier propiedad se puede sobrescribir sin tocar el código, con variables de entorno o argumentos:

```bash
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar --server.port=8080 --spring.mongodb.host=otra-maquina
# o bien
SPRING_DATASOURCE_URL=jdbc:mariadb://otra-maquina:3306/persona_db java -jar ...
```

---

## 2. Compilar

Desde la raíz:

```bash
mvn clean install
```

Compila los 7 módulos en orden, ejecuta las pruebas unitarias (dominio y casos de uso, sin bases de datos) y genera dos JAR ejecutables:

- `rest-input-adapter/target/rest-input-adapter-1.0.0.jar`
- `cli-input-adapter/target/cli-input-adapter-1.0.0.jar`

Para compilar sin pruebas: `mvn clean install -DskipTests`.

---

## 3. Desplegar y ejecutar

Las bases de datos deben estar arriba (paso 1).

### API REST + Swagger

```bash
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar
```

- Swagger UI: **http://localhost:3000/swagger-ui.html**
- Especificación OpenAPI (JSON): http://localhost:3000/api-docs
- Logs: `logs/persona-rest.log`

### CLI (consola)

```bash
java -jar cli-input-adapter/target/cli-input-adapter-1.0.0.jar
```

Menú principal → módulo (1 Personas, 2 Profesiones, 3 Teléfonos, 4 Estudios) → motor (1 MariaDB, 2 MongoDB) → operación (ver todo, buscar, crear, editar, eliminar, contar…). Logs: `logs/persona-cli.log`.

### Desde el IDE

Ejecutar la clase `PersonAppRestApi` (módulo `rest-input-adapter`) o `PersonAppCli` (módulo `cli-input-adapter`). Son **dos `SpringApplication` distintas**, una por adaptador de entrada.

---

## Endpoints REST

`{database}` es `MARIA` o `MONGO` (también se acepta `MariaDB` / `MongoDB`, sin importar mayúsculas). En `POST` y `PUT` el motor va en el campo `database` del cuerpo JSON.

| Recurso | Método | Ruta | Descripción |
|---|---|---|---|
| Persona | GET | `/api/v1/persona/{database}` | Listar |
| | GET | `/api/v1/persona/{database}/count` | Contar |
| | GET | `/api/v1/persona/{database}/{cc}` | Buscar por cédula |
| | GET | `/api/v1/persona/{database}/{cc}/telefonos` | Teléfonos de la persona |
| | GET | `/api/v1/persona/{database}/{cc}/estudios` | Estudios de la persona |
| | POST | `/api/v1/persona` | Crear |
| | PUT | `/api/v1/persona/{cc}` | Editar |
| | DELETE | `/api/v1/persona/{database}/{cc}` | Eliminar (con sus teléfonos y estudios) |
| Profesión | GET | `/api/v1/profesion/{database}` | Listar |
| | GET | `/api/v1/profesion/{database}/count` | Contar |
| | GET | `/api/v1/profesion/{database}/{id}` | Buscar |
| | GET | `/api/v1/profesion/{database}/{id}/estudios` | Quiénes estudiaron esa profesión |
| | POST | `/api/v1/profesion` | Crear |
| | PUT | `/api/v1/profesion/{id}` | Editar |
| | DELETE | `/api/v1/profesion/{database}/{id}` | Eliminar (con sus estudios) |
| Teléfono | GET | `/api/v1/telefono/{database}` | Listar |
| | GET | `/api/v1/telefono/{database}/count` | Contar |
| | GET | `/api/v1/telefono/{database}/{numero}` | Buscar |
| | POST | `/api/v1/telefono` | Crear (el dueño debe existir) |
| | PUT | `/api/v1/telefono/{numero}` | Editar operador o dueño |
| | DELETE | `/api/v1/telefono/{database}/{numero}` | Eliminar |
| Estudio | GET | `/api/v1/estudio/{database}` | Listar |
| | GET | `/api/v1/estudio/{database}/count` | Contar |
| | GET | `/api/v1/estudio/{database}/{ccPersona}/{idProfesion}` | Buscar por llave compuesta |
| | POST | `/api/v1/estudio` | Crear (persona y profesión deben existir) |
| | PUT | `/api/v1/estudio/{ccPersona}/{idProfesion}` | Editar fecha y universidad |
| | DELETE | `/api/v1/estudio/{database}/{ccPersona}/{idProfesion}` | Eliminar |

**Códigos de respuesta**: `200` OK, `201` creado, `400` datos inválidos o motor inválido, `404` no existe, `409` ya existe, `500` error inesperado. Los errores responden con `{ "status", "description", "date" }`.

### Ejemplos con curl

```bash
# Crear una persona en MariaDB
curl -X POST http://localhost:3000/api/v1/persona -H "Content-Type: application/json" \
  -d '{"dni":"1010101010","firstName":"Laura","lastName":"Gomez","age":"25","sex":"F","database":"MARIA"}'

# La misma persona, pero en MongoDB (mismo programa, otro motor, sin reiniciar)
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

---

## Cambio de base de datos en caliente

El mismo puerto de salida (por ejemplo `PersonOutputPort`) tiene **dos implementaciones**, registradas como beans con nombre:

- `personOutputAdapterMaria` (`@Adapter("personOutputAdapterMaria")`)
- `personOutputAdapterMongo` (`@Adapter("personOutputAdapterMongo")`)

Como hay dos beans del mismo tipo, un simple `@Autowired` no sabría cuál inyectar. Por eso el adaptador de entrada recibe **ambos** por constructor con `@Qualifier`, y en **cada petición** construye el caso de uso con el que pidió el cliente:

```java
private PersonInputPort setPersonOutputPortInjection(DatabaseOption dbOption) {
    return dbOption == DatabaseOption.MARIA ? new PersonUseCase(personOutputPortMaria)
                                            : new PersonUseCase(personOutputPortMongo);
}
```

- **Tiempo de compilación** sería activar o desactivar una clase y recompilar (o reiniciar con otro perfil).
- **Tiempo de ejecución** (lo implementado) es un parámetro que llega con la petición: el sistema no se apaga.

Es la base del patrón **CQRS** (escribir en una base y leer de otra).

---

## Cambios respecto a la plantilla

**Errores que impedían que funcionara:**

1. `EstudiosMapperMaria.fromAdapterToDomain` y `EstudiosMapperMongo.fromAdapterToDomain` hacían `return null` → toda persona con estudios fallaba.
2. Recursión infinita en los mappers (persona → teléfono → persona → …) → `StackOverflowError`. Se resolvió con un mapeo "básico" sin listas (`toDomainBasic`) para las referencias.
3. `PersonaMapperRest.fromAdapterToDomain` devolvía `new Person()` vacío → crear por REST nunca funcionaba.
4. `persona_dml_mongo.js` usaba `use prueba_db` en lugar de `persona_db`, y los scripts `.js` usaban `use`, que `mongosh` no acepta en archivos.
5. Las dependencias circulares entre mappers obligaban a `spring.main.allow-circular-references=true`; al eliminarlas ya no hace falta.
6. Comparaciones `Character == 'F'` con posible `null` (unboxing) en el mapper de MariaDB.

**Migración de versiones (Java 11 / Boot 2.7 → Java 21 / Boot 4.1):**

- `javax.persistence.*` → `jakarta.persistence.*`; `javax.transaction.Transactional` → el `@Transactional` de Spring.
- `@Temporal` con `java.util.Date` → `LocalDate` directo; `@Lob` sobre `TEXT` → `@Column(columnDefinition = "TEXT")`.
- `springdoc-openapi-ui 1.x` → `springdoc-openapi-starter-webmvc-ui 3.x`; `spring-boot-starter-web` → `spring-boot-starter-webmvc`.
- Propiedades `spring.data.mongodb.*` → `spring.mongodb.*`.
- Versiones centralizadas con el BOM `spring-boot-dependencies`; Lombok declarado como *annotation processor*; `-parameters` habilitado; `spring-boot-maven-plugin` con `repackage` para generar JAR ejecutables.
- Se quitaron dependencias que ya no aplican (JAXB 2.3 del CLI, `log4j-to-slf4j` explícito).

**Funcionalidad nueva:** puertos, casos de uso y adaptadores (MariaDB, MongoDB, REST y CLI) de profesión, teléfono y estudios; validaciones de negocio; manejo global de errores HTTP; pruebas unitarias; `docker-compose.yml`; integración continua (GitHub Actions).

---

## Pruebas

### Pruebas unitarias

```bash
mvn test
```

Las pruebas de `domain` y `application` usan **adaptadores falsos en memoria** (implementan los puertos de salida). No necesitan MariaDB, MongoDB ni Spring: es la ventaja práctica de la arquitectura hexagonal.

### Pruebas de integración (API REST y CLI contra las bases reales)

Con las bases arriba (`docker compose up -d`) y el proyecto compilado:

```bash
# API REST: arrancarla y ejecutar el CRUD completo en MariaDB y luego en MongoDB, sin reiniciarla
java -jar rest-input-adapter/target/rest-input-adapter-1.0.0.jar &
bash scripts/prueba-integracion.sh http://localhost:3000

# CLI: alimenta el menú con una secuencia de opciones y verifica la salida
bash scripts/prueba-cli.sh cli-input-adapter/target/cli-input-adapter-1.0.0.jar
```

`prueba-integracion.sh` (requiere `curl` y `jq`) verifica en **ambos motores**: lectura de los datos de los scripts DML, creación, edición y eliminación de las 4 entidades, borrado en cascada, códigos de error (400, 404, 409), fechas sin desfase y que Swagger/OpenAPI publique los 23 recursos. Al terminar deja las bases como estaban.

### Integración continua

En cada *push*, [GitHub Actions](.github/workflows/build.yml) compila con JDK 21, ejecuta las pruebas unitarias, levanta MariaDB y MongoDB con `docker compose` (que ejecuta los scripts DDL y DML) y corre las dos pruebas de integración. El resumen de cada ejecución muestra la tabla de resultados y el artefacto **evidencias** guarda capturas de Swagger UI, la sesión de la CLI y los registros.

---

## Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| El IDE marca `getX()`/`setX()` como inexistentes | Lombok no configurado | Instalar el plugin de Lombok y habilitar *annotation processing* |
| `Access denied for user 'persona_db'` | No se ejecutó el DDL de MariaDB | Ejecutar `persona_ddl_maria.sql` como root |
| `Command failed with error 18 (AuthenticationFailed)` en MongoDB | No existe el usuario `persona_db` en `admin` | Ejecutar `persona_ddl_mongo.js` |
| `Connection refused` a `localhost:3307` | MariaDB no está en el puerto 3307 | Revisar el contenedor (`docker compose ps`) o el puerto de la instalación |
| `Port 3000 was already in use` | Otro proceso usa el puerto | `--server.port=8080` |
| Los datos de prueba no aparecen en Docker | Los volúmenes ya existían | `docker compose down -v` y `docker compose up -d` |
| `release version 21 not supported` | Maven usa un JDK viejo | Apuntar `JAVA_HOME` al JDK 21 |

---

## Entrega

- Repositorio: este repositorio (fork de la plantilla).
- Versión entregada: tag **`v1.0.0`**.
- Scripts DDL y DML: carpeta [`scripts/`](scripts).

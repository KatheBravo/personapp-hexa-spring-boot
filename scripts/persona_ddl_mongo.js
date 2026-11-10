// =====================================================================
// PersonApp - DDL MongoDB
// Crea el usuario persona_db (en la base admin), las 4 colecciones con su
// esquema ($jsonSchema) y los indices de las referencias.
// Ejecutar:  mongosh "mongodb://localhost:27017" persona_ddl_mongo.js
//   (si MongoDB tiene autenticacion: mongosh -u root -p root --authenticationDatabase admin persona_ddl_mongo.js)
// Se usa db.getSiblingDB(...) en lugar de "use ..." porque los archivos .js
// que ejecuta mongosh no aceptan los comandos de shell como "use".
// =====================================================================
var admin = db.getSiblingDB("admin");

if (admin.getUser("persona_db") !== null) {
  admin.dropUser("persona_db");
}

admin.createUser({
  user: "persona_db",
  pwd: "persona_db",
  roles: [
    { role: "read", db: "persona_db" },
    { role: "readWrite", db: "persona_db" },
    { role: "dbAdmin", db: "persona_db" }
  ],
  mechanisms: ["SCRAM-SHA-1", "SCRAM-SHA-256"]
});

var personaDb = db.getSiblingDB("persona_db");
personaDb.dropDatabase();

// validationAction "warn": el esquema documenta el modelo y MongoDB registra
// en su log los documentos que no lo cumplan, sin bloquear la escritura.
// Para hacerlo estricto basta con cambiarlo a "error".
var opciones = (schema) => ({
  validator: { $jsonSchema: schema },
  validationLevel: "moderate",
  validationAction: "warn"
});

personaDb.createCollection("persona", opciones({
  bsonType: "object",
  required: ["_id", "nombre", "apellido", "genero"],
  properties: {
    _id: { bsonType: "int", description: "cedula" },
    nombre: { bsonType: "string", maxLength: 45 },
    apellido: { bsonType: "string", maxLength: 45 },
    genero: { enum: ["M", "F"] },
    edad: { bsonType: ["int", "null"], minimum: 0 }
  }
}));

personaDb.createCollection("profesion", opciones({
  bsonType: "object",
  required: ["_id", "nom"],
  properties: {
    _id: { bsonType: "int" },
    nom: { bsonType: "string", maxLength: 90 },
    des: { bsonType: ["string", "null"] }
  }
}));

personaDb.createCollection("telefono", opciones({
  bsonType: "object",
  required: ["_id", "oper", "primaryDuenio"],
  properties: {
    _id: { bsonType: "string", pattern: "^[0-9]{7,15}$", description: "numero" },
    oper: { bsonType: "string", maxLength: 45 },
    primaryDuenio: { bsonType: "int", description: "referencia a persona._id" }
  }
}));

personaDb.createCollection("estudios", opciones({
  bsonType: "object",
  required: ["_id", "primaryPersona", "primaryProfesion"],
  properties: {
    _id: { bsonType: "string", description: "cedula-idProfesion" },
    primaryPersona: { bsonType: "int", description: "referencia a persona._id" },
    primaryProfesion: { bsonType: "int", description: "referencia a profesion._id" },
    fecha: { bsonType: ["date", "null"] },
    univer: { bsonType: ["string", "null"], maxLength: 50 }
  }
}));

// Indices para las busquedas por referencia (telefonos y estudios de una persona)
personaDb.telefono.createIndex({ primaryDuenio: 1 }, { name: "telefono_persona_idx" });
personaDb.estudios.createIndex({ primaryPersona: 1 }, { name: "estudio_persona_idx" });
personaDb.estudios.createIndex({ primaryProfesion: 1 }, { name: "estudio_profesion_idx" });
personaDb.estudios.createIndex({ primaryPersona: 1, primaryProfesion: 1 }, { name: "estudios_pk_idx", unique: true });

print("persona_db creada con usuario persona_db y colecciones: " + personaDb.getCollectionNames().join(", "));

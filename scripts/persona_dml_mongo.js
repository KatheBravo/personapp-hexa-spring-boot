// =====================================================================
// PersonApp - DML MongoDB (datos de prueba, los mismos de MariaDB)
// Ejecutar despues del DDL:  mongosh "mongodb://localhost:27017" persona_dml_mongo.js
// - NumberInt(...) guarda enteros de 32 bits, como los Integer de Java.
// - Las referencias (primaryDuenio, primaryPersona, primaryProfesion) guardan
//   solo el _id del documento referenciado: asi trabaja @DocumentReference.
// - _class le dice a Spring Data que clase Java corresponde a cada documento.
// =====================================================================
var personaDb = db.getSiblingDB("persona_db");
var pkg = "co.edu.javeriana.as.personapp.mongo.document.";

personaDb.persona.insertMany([
  { _id: NumberInt(123456789), nombre: "Pepe", apellido: "Perez", genero: "M", edad: NumberInt(30), _class: pkg + "PersonaDocument" },
  { _id: NumberInt(987654321), nombre: "Pepito", apellido: "Perez", genero: "M", _class: pkg + "PersonaDocument" },
  { _id: NumberInt(321654987), nombre: "Pepa", apellido: "Juarez", genero: "F", edad: NumberInt(30), _class: pkg + "PersonaDocument" },
  { _id: NumberInt(147258369), nombre: "Pepita", apellido: "Juarez", genero: "F", edad: NumberInt(10), _class: pkg + "PersonaDocument" },
  { _id: NumberInt(963852741), nombre: "Fede", apellido: "Perez", genero: "M", edad: NumberInt(18), _class: pkg + "PersonaDocument" }
], { ordered: false });

personaDb.profesion.insertMany([
  { _id: NumberInt(1), nom: "Ingeniería de Sistemas", des: "Diseña, construye y mantiene sistemas de software", _class: pkg + "ProfesionDocument" },
  { _id: NumberInt(2), nom: "Medicina", des: "Previene, diagnostica y trata enfermedades", _class: pkg + "ProfesionDocument" },
  { _id: NumberInt(3), nom: "Derecho", des: "Asesora y representa en asuntos legales", _class: pkg + "ProfesionDocument" },
  { _id: NumberInt(4), nom: "Arquitectura", des: "Diseña espacios y edificaciones", _class: pkg + "ProfesionDocument" },
  { _id: NumberInt(5), nom: "Contaduría Pública", des: "Lleva la contabilidad y la información financiera", _class: pkg + "ProfesionDocument" }
], { ordered: false });

personaDb.telefono.insertMany([
  { _id: "3001234567", oper: "Claro", primaryDuenio: NumberInt(123456789), _class: pkg + "TelefonoDocument" },
  { _id: "3109876543", oper: "Movistar", primaryDuenio: NumberInt(123456789), _class: pkg + "TelefonoDocument" },
  { _id: "3205551234", oper: "Tigo", primaryDuenio: NumberInt(321654987), _class: pkg + "TelefonoDocument" },
  { _id: "3157778899", oper: "Claro", primaryDuenio: NumberInt(963852741), _class: pkg + "TelefonoDocument" },
  { _id: "6012345678", oper: "ETB", primaryDuenio: NumberInt(987654321), _class: pkg + "TelefonoDocument" }
], { ordered: false });

// Las fechas van en UTC a medianoche: el adaptador usa los codecs nativos del
// driver (MongoConfig), que leen un LocalDate en UTC.
personaDb.estudios.insertMany([
  { _id: "123456789-1", primaryPersona: NumberInt(123456789), primaryProfesion: NumberInt(1), fecha: ISODate("2015-12-01T00:00:00Z"), univer: "Pontificia Universidad Javeriana", _class: pkg + "EstudiosDocument" },
  { _id: "123456789-4", primaryPersona: NumberInt(123456789), primaryProfesion: NumberInt(4), fecha: ISODate("2019-06-15T00:00:00Z"), univer: "Universidad Nacional de Colombia", _class: pkg + "EstudiosDocument" },
  { _id: "321654987-2", primaryPersona: NumberInt(321654987), primaryProfesion: NumberInt(2), fecha: ISODate("2018-11-30T00:00:00Z"), univer: "Universidad del Rosario", _class: pkg + "EstudiosDocument" },
  { _id: "987654321-5", primaryPersona: NumberInt(987654321), primaryProfesion: NumberInt(5), fecha: ISODate("2010-07-20T00:00:00Z"), univer: "Universidad de los Andes", _class: pkg + "EstudiosDocument" },
  { _id: "963852741-3", primaryPersona: NumberInt(963852741), primaryProfesion: NumberInt(3), univer: "Universidad Externado de Colombia", _class: pkg + "EstudiosDocument" }
], { ordered: false });

print("Datos cargados: " + personaDb.persona.countDocuments() + " personas, "
  + personaDb.profesion.countDocuments() + " profesiones, "
  + personaDb.telefono.countDocuments() + " telefonos, "
  + personaDb.estudios.countDocuments() + " estudios");

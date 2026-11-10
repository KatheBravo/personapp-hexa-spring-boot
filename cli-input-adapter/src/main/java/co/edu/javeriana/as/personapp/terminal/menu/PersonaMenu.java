package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonaMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_VER_TODO = 1;
	private static final int OPCION_BUSCAR = 2;
	private static final int OPCION_CREAR = 3;
	private static final int OPCION_EDITAR = 4;
	private static final int OPCION_ELIMINAR = 5;
	private static final int OPCION_CONTAR = 6;
	private static final int OPCION_VER_TELEFONOS = 7;
	private static final int OPCION_VER_ESTUDIOS = 8;

	public void iniciarMenu(PersonaInputAdapterCli personaInputAdapterCli, LectorConsola lector) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuMotorPersistencia();
				int opcion = lector.leerOpcion();
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					isValid = true;
					break;
				case PERSISTENCIA_MARIADB:
					personaInputAdapterCli.setPersonOutputPortInjection("MARIA");
					menuOpciones(personaInputAdapterCli, lector);
					break;
				case PERSISTENCIA_MONGODB:
					personaInputAdapterCli.setPersonOutputPortInjection("MONGO");
					menuOpciones(personaInputAdapterCli, lector);
					break;
				default:
					System.out.println("La opcion elegida no es valida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(PersonaInputAdapterCli personaInputAdapterCli, LectorConsola lector) {
		boolean isValid = false;
		do {
			mostrarMenuOpciones(personaInputAdapterCli.motorActual());
			int opcion = lector.leerOpcion();
			switch (opcion) {
			case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
				isValid = true;
				break;
			case OPCION_VER_TODO:
				personaInputAdapterCli.historial();
				break;
			case OPCION_BUSCAR:
				personaInputAdapterCli.buscar(lector.leerEntero("Cedula: "));
				break;
			case OPCION_CREAR:
				personaInputAdapterCli.crear(leerPersona(lector, lector.leerEntero("Cedula: ")));
				break;
			case OPCION_EDITAR:
				Integer cc = lector.leerEntero("Cedula de la persona a editar: ");
				personaInputAdapterCli.editar(leerPersona(lector, cc));
				break;
			case OPCION_ELIMINAR:
				personaInputAdapterCli.eliminar(lector.leerEntero("Cedula de la persona a eliminar: "));
				break;
			case OPCION_CONTAR:
				personaInputAdapterCli.contar();
				break;
			case OPCION_VER_TELEFONOS:
				personaInputAdapterCli.telefonos(lector.leerEntero("Cedula: "));
				break;
			case OPCION_VER_ESTUDIOS:
				personaInputAdapterCli.estudios(lector.leerEntero("Cedula: "));
				break;
			default:
				System.out.println("La opcion elegida no es valida.");
			}
		} while (!isValid);
	}

	private PersonaModelCli leerPersona(LectorConsola lector, Integer cc) {
		PersonaModelCli persona = new PersonaModelCli();
		persona.setCc(cc);
		persona.setNombre(lector.leerTexto("Nombre: "));
		persona.setApellido(lector.leerTexto("Apellido: "));
		persona.setGenero(lector.leerGenero("Genero (M/F): "));
		persona.setEdad(lector.leerEnteroOpcional("Edad (Enter para omitir): "));
		return persona;
	}

	private void mostrarMenuOpciones(String motor) {
		System.out.println("---------------------- Personas [" + motor + "]");
		System.out.println(OPCION_VER_TODO + " para ver todas las personas");
		System.out.println(OPCION_BUSCAR + " para buscar una persona");
		System.out.println(OPCION_CREAR + " para crear una persona");
		System.out.println(OPCION_EDITAR + " para editar una persona");
		System.out.println(OPCION_ELIMINAR + " para eliminar una persona (y sus telefonos y estudios)");
		System.out.println(OPCION_CONTAR + " para contar las personas");
		System.out.println(OPCION_VER_TELEFONOS + " para ver los telefonos de una persona");
		System.out.println(OPCION_VER_ESTUDIOS + " para ver los estudios de una persona");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("---------------------- Personas: motor de persistencia");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

}

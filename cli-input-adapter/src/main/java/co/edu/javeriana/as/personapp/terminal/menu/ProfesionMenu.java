package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Menú de consola del módulo de profesiones. Primero se elige el motor de
 * persistencia y después la operación.
 */
@Slf4j
public class ProfesionMenu {

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
	private static final int OPCION_VER_ESTUDIOS = 7;

	/**
	 * Muestra el menú de motores hasta que el usuario regresa al menú principal.
	 *
	 * @param profesionInputAdapterCli adaptador que ejecuta las operaciones
	 * @param lector lector de la entrada estándar
	 */
	public void iniciarMenu(ProfesionInputAdapterCli profesionInputAdapterCli, LectorConsola lector) {
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
					profesionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
					menuOpciones(profesionInputAdapterCli, lector);
					break;
				case PERSISTENCIA_MONGODB:
					profesionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
					menuOpciones(profesionInputAdapterCli, lector);
					break;
				default:
					System.out.println("La opcion elegida no es valida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	/**
	 * Muestra las operaciones del módulo para el motor elegido hasta que el usuario
	 * regresa.
	 *
	 * @param profesionInputAdapterCli adaptador conectado al motor elegido
	 * @param lector lector de la entrada estándar
	 */
	private void menuOpciones(ProfesionInputAdapterCli profesionInputAdapterCli, LectorConsola lector) {
		boolean isValid = false;
		do {
			mostrarMenuOpciones(profesionInputAdapterCli.motorActual());
			int opcion = lector.leerOpcion();
			switch (opcion) {
			case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
				isValid = true;
				break;
			case OPCION_VER_TODO:
				profesionInputAdapterCli.historial();
				break;
			case OPCION_BUSCAR:
				profesionInputAdapterCli.buscar(lector.leerEntero("Id de la profesion: "));
				break;
			case OPCION_CREAR:
				profesionInputAdapterCli.crear(leerProfesion(lector, lector.leerEntero("Id de la profesion: ")));
				break;
			case OPCION_EDITAR:
				Integer id = lector.leerEntero("Id de la profesion a editar: ");
				profesionInputAdapterCli.editar(leerProfesion(lector, id));
				break;
			case OPCION_ELIMINAR:
				profesionInputAdapterCli.eliminar(lector.leerEntero("Id de la profesion a eliminar: "));
				break;
			case OPCION_CONTAR:
				profesionInputAdapterCli.contar();
				break;
			case OPCION_VER_ESTUDIOS:
				profesionInputAdapterCli.estudios(lector.leerEntero("Id de la profesion: "));
				break;
			default:
				System.out.println("La opcion elegida no es valida.");
			}
		} while (!isValid);
	}

	/**
	 * Lee de la consola los datos de una profesión.
	 *
	 * @param lector lector de la entrada estándar
	 * @param id identificador de la profesión, leído antes
	 * @return datos ingresados; la descripción puede quedar vacía
	 */
	private ProfesionModelCli leerProfesion(LectorConsola lector, Integer id) {
		ProfesionModelCli profesion = new ProfesionModelCli();
		profesion.setId(id);
		profesion.setNombre(lector.leerTexto("Nombre: "));
		profesion.setDescripcion(lector.leerTextoOpcional("Descripcion (Enter para omitir): "));
		return profesion;
	}

	/**
	 * Imprime las operaciones disponibles.
	 *
	 * @param motor nombre del motor en uso
	 */
	private void mostrarMenuOpciones(String motor) {
		System.out.println("---------------------- Profesiones [" + motor + "]");
		System.out.println(OPCION_VER_TODO + " para ver todas las profesiones");
		System.out.println(OPCION_BUSCAR + " para buscar una profesion");
		System.out.println(OPCION_CREAR + " para crear una profesion");
		System.out.println(OPCION_EDITAR + " para editar una profesion");
		System.out.println(OPCION_ELIMINAR + " para eliminar una profesion (y sus estudios)");
		System.out.println(OPCION_CONTAR + " para contar las profesiones");
		System.out.println(OPCION_VER_ESTUDIOS + " para ver quienes estudiaron una profesion");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	/**
	 * Imprime los motores de persistencia disponibles.
	 */
	private void mostrarMenuMotorPersistencia() {
		System.out.println("---------------------- Profesiones: motor de persistencia");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}
}

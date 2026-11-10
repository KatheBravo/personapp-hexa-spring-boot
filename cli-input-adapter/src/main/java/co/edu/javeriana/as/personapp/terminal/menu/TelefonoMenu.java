package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Menú de consola del módulo de teléfonos. Primero se elige el motor de
 * persistencia y después la operación.
 */
@Slf4j
public class TelefonoMenu {

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

	/**
	 * Muestra el menú de motores hasta que el usuario regresa al menú principal.
	 *
	 * @param telefonoInputAdapterCli adaptador que ejecuta las operaciones
	 * @param lector lector de la entrada estándar
	 */
	public void iniciarMenu(TelefonoInputAdapterCli telefonoInputAdapterCli, LectorConsola lector) {
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
					telefonoInputAdapterCli.setPhoneOutputPortInjection("MARIA");
					menuOpciones(telefonoInputAdapterCli, lector);
					break;
				case PERSISTENCIA_MONGODB:
					telefonoInputAdapterCli.setPhoneOutputPortInjection("MONGO");
					menuOpciones(telefonoInputAdapterCli, lector);
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
	 * @param telefonoInputAdapterCli adaptador conectado al motor elegido
	 * @param lector lector de la entrada estándar
	 */
	private void menuOpciones(TelefonoInputAdapterCli telefonoInputAdapterCli, LectorConsola lector) {
		boolean isValid = false;
		do {
			mostrarMenuOpciones(telefonoInputAdapterCli.motorActual());
			int opcion = lector.leerOpcion();
			switch (opcion) {
			case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
				isValid = true;
				break;
			case OPCION_VER_TODO:
				telefonoInputAdapterCli.historial();
				break;
			case OPCION_BUSCAR:
				telefonoInputAdapterCli.buscar(lector.leerTexto("Numero: "));
				break;
			case OPCION_CREAR:
				telefonoInputAdapterCli.crear(leerTelefono(lector, lector.leerTexto("Numero: ")));
				break;
			case OPCION_EDITAR:
				String numero = lector.leerTexto("Numero del telefono a editar: ");
				telefonoInputAdapterCli.editar(leerTelefono(lector, numero));
				break;
			case OPCION_ELIMINAR:
				telefonoInputAdapterCli.eliminar(lector.leerTexto("Numero del telefono a eliminar: "));
				break;
			case OPCION_CONTAR:
				telefonoInputAdapterCli.contar();
				break;
			default:
				System.out.println("La opcion elegida no es valida.");
			}
		} while (!isValid);
	}

	/**
	 * Lee de la consola los datos de un teléfono.
	 *
	 * @param lector lector de la entrada estándar
	 * @param numero número del teléfono, leído antes
	 * @return datos ingresados
	 */
	private TelefonoModelCli leerTelefono(LectorConsola lector, String numero) {
		TelefonoModelCli telefono = new TelefonoModelCli();
		telefono.setNumero(numero);
		telefono.setOperador(lector.leerTexto("Operador: "));
		telefono.setCcDuenio(lector.leerEntero("Cedula del duenio: "));
		return telefono;
	}

	/**
	 * Imprime las operaciones disponibles.
	 *
	 * @param motor nombre del motor en uso
	 */
	private void mostrarMenuOpciones(String motor) {
		System.out.println("---------------------- Telefonos [" + motor + "]");
		System.out.println(OPCION_VER_TODO + " para ver todos los telefonos");
		System.out.println(OPCION_BUSCAR + " para buscar un telefono");
		System.out.println(OPCION_CREAR + " para crear un telefono");
		System.out.println(OPCION_EDITAR + " para editar un telefono");
		System.out.println(OPCION_ELIMINAR + " para eliminar un telefono");
		System.out.println(OPCION_CONTAR + " para contar los telefonos");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	/**
	 * Imprime los motores de persistencia disponibles.
	 */
	private void mostrarMenuMotorPersistencia() {
		System.out.println("---------------------- Telefonos: motor de persistencia");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}
}

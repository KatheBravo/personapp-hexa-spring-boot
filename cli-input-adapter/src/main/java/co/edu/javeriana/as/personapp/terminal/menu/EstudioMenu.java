package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstudioMenu {

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

	public void iniciarMenu(EstudioInputAdapterCli estudioInputAdapterCli, LectorConsola lector) {
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
					estudioInputAdapterCli.setStudyOutputPortInjection("MARIA");
					menuOpciones(estudioInputAdapterCli, lector);
					break;
				case PERSISTENCIA_MONGODB:
					estudioInputAdapterCli.setStudyOutputPortInjection("MONGO");
					menuOpciones(estudioInputAdapterCli, lector);
					break;
				default:
					System.out.println("La opcion elegida no es valida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(EstudioInputAdapterCli estudioInputAdapterCli, LectorConsola lector) {
		boolean isValid = false;
		do {
			mostrarMenuOpciones(estudioInputAdapterCli.motorActual());
			int opcion = lector.leerOpcion();
			switch (opcion) {
			case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
				isValid = true;
				break;
			case OPCION_VER_TODO:
				estudioInputAdapterCli.historial();
				break;
			case OPCION_BUSCAR:
				estudioInputAdapterCli.buscar(lector.leerEntero("Cedula de la persona: "),
						lector.leerEntero("Id de la profesion: "));
				break;
			case OPCION_CREAR:
				estudioInputAdapterCli.crear(leerEstudio(lector));
				break;
			case OPCION_EDITAR:
				System.out.println("(La persona y la profesion identifican el estudio; se editan fecha y universidad)");
				estudioInputAdapterCli.editar(leerEstudio(lector));
				break;
			case OPCION_ELIMINAR:
				estudioInputAdapterCli.eliminar(lector.leerEntero("Cedula de la persona: "),
						lector.leerEntero("Id de la profesion: "));
				break;
			case OPCION_CONTAR:
				estudioInputAdapterCli.contar();
				break;
			default:
				System.out.println("La opcion elegida no es valida.");
			}
		} while (!isValid);
	}

	private EstudioModelCli leerEstudio(LectorConsola lector) {
		EstudioModelCli estudio = new EstudioModelCli();
		estudio.setCcPersona(lector.leerEntero("Cedula de la persona: "));
		estudio.setIdProfesion(lector.leerEntero("Id de la profesion: "));
		estudio.setFechaGrado(lector.leerFechaOpcional("Fecha de grado yyyy-MM-dd (Enter para omitir): "));
		estudio.setUniversidad(lector.leerTextoOpcional("Universidad (Enter para omitir): "));
		return estudio;
	}

	private void mostrarMenuOpciones(String motor) {
		System.out.println("---------------------- Estudios [" + motor + "]");
		System.out.println(OPCION_VER_TODO + " para ver todos los estudios");
		System.out.println(OPCION_BUSCAR + " para buscar un estudio");
		System.out.println(OPCION_CREAR + " para crear un estudio");
		System.out.println(OPCION_EDITAR + " para editar un estudio");
		System.out.println(OPCION_ELIMINAR + " para eliminar un estudio");
		System.out.println(OPCION_CONTAR + " para contar los estudios");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("---------------------- Estudios: motor de persistencia");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}
}

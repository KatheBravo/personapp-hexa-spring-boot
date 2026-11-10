package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Menú principal de la consola. Muestra los módulos de personas, profesiones,
 * teléfonos y estudios, y delega en el menú de cada uno.
 */
@Slf4j
@Controller
public class MenuPrincipal {

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	/** Adaptador de entrada de personas. */
	private final PersonaInputAdapterCli personaInputAdapterCli;

	/** Adaptador de entrada de profesiones. */
	private final ProfesionInputAdapterCli profesionInputAdapterCli;

	/** Adaptador de entrada de teléfonos. */
	private final TelefonoInputAdapterCli telefonoInputAdapterCli;

	/** Adaptador de entrada de estudios. */
	private final EstudioInputAdapterCli estudioInputAdapterCli;

	/** Menú del módulo de personas. */
	private final PersonaMenu personaMenu;

	/** Menú del módulo de profesiones. */
	private final ProfesionMenu profesionMenu;

	/** Menú del módulo de teléfonos. */
	private final TelefonoMenu telefonoMenu;

	/** Menú del módulo de estudios. */
	private final EstudioMenu estudioMenu;

	/** Lector de la entrada estándar, compartido por todos los menús. */
	private final LectorConsola lector;

	/**
	 * Recibe los adaptadores de entrada y crea los menús y el lector de la entrada
	 * estándar.
	 *
	 * @param personaInputAdapterCli adaptador de personas
	 * @param profesionInputAdapterCli adaptador de profesiones
	 * @param telefonoInputAdapterCli adaptador de teléfonos
	 * @param estudioInputAdapterCli adaptador de estudios
	 */
	public MenuPrincipal(PersonaInputAdapterCli personaInputAdapterCli,
			ProfesionInputAdapterCli profesionInputAdapterCli, TelefonoInputAdapterCli telefonoInputAdapterCli,
			EstudioInputAdapterCli estudioInputAdapterCli) {
		this.personaInputAdapterCli = personaInputAdapterCli;
		this.profesionInputAdapterCli = profesionInputAdapterCli;
		this.telefonoInputAdapterCli = telefonoInputAdapterCli;
		this.estudioInputAdapterCli = estudioInputAdapterCli;
		this.personaMenu = new PersonaMenu();
		this.profesionMenu = new ProfesionMenu();
		this.telefonoMenu = new TelefonoMenu();
		this.estudioMenu = new EstudioMenu();
		this.lector = new LectorConsola(new Scanner(System.in));
	}

	/**
	 * Ejecuta el menú principal hasta que el usuario elige salir o se cierra la
	 * entrada estándar. Un error en un módulo, como una base de datos apagada, se
	 * informa y se vuelve al menú.
	 */
	public void inicio() {
		boolean isValid = false;
		try {
			do {
				mostrarMenu();
				int opcion = lector.leerOpcion();
				try {
					switch (opcion) {
					case SALIR:
						isValid = true;
						break;
					case MODULO_PERSONA:
						personaMenu.iniciarMenu(personaInputAdapterCli, lector);
						break;
					case MODULO_PROFESION:
						profesionMenu.iniciarMenu(profesionInputAdapterCli, lector);
						break;
					case MODULO_TELEFONO:
						telefonoMenu.iniciarMenu(telefonoInputAdapterCli, lector);
						break;
					case MODULO_ESTUDIO:
						estudioMenu.iniciarMenu(estudioInputAdapterCli, lector);
						break;
					default:
						System.out.println("La opcion elegida no es valida.");
					}
				} catch (NoSuchElementException e) {
					throw e;
				} catch (RuntimeException e) {
					// Por ejemplo, una base de datos apagada: se informa el error y se vuelve al menú
					log.error("Error en el modulo elegido", e);
					System.out.println("Error: " + e.getMessage());
				}
			} while (!isValid);
		} catch (NoSuchElementException e) {
			// La entrada estándar se cerró (Ctrl+D o Ctrl+Z): se termina sin error
			log.info("Entrada cerrada, saliendo de PersonApp CLI");
		}
		System.out.println("Hasta luego.");
	}

	/**
	 * Imprime los módulos disponibles.
	 */
	private void mostrarMenu() {
		System.out.println("======================");
		System.out.println("      PersonApp CLI   ");
		System.out.println("======================");
		System.out.println(MODULO_PERSONA + " para trabajar con el Modulo de Personas");
		System.out.println(MODULO_PROFESION + " para trabajar con el Modulo de Profesiones");
		System.out.println(MODULO_TELEFONO + " para trabajar con el Modulo de Telefonos");
		System.out.println(MODULO_ESTUDIO + " para trabajar con el Modulo de Estudios");
		System.out.println(SALIR + " para Salir");
	}

}

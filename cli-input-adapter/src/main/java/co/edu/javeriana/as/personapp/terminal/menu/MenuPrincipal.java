package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MenuPrincipal {

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	// Beans (adaptadores de entrada CLI)
	private final PersonaInputAdapterCli personaInputAdapterCli;
	private final ProfesionInputAdapterCli profesionInputAdapterCli;
	private final TelefonoInputAdapterCli telefonoInputAdapterCli;
	private final EstudioInputAdapterCli estudioInputAdapterCli;

	// Menus
	private final PersonaMenu personaMenu;
	private final ProfesionMenu profesionMenu;
	private final TelefonoMenu telefonoMenu;
	private final EstudioMenu estudioMenu;
	private final LectorConsola lector;

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
					// Por ejemplo, si una base de datos esta apagada: se informa y se vuelve al menu principal
					log.error("Error en el modulo elegido", e);
					System.out.println("Error: " + e.getMessage());
				}
			} while (!isValid);
		} catch (NoSuchElementException e) {
			// La entrada estandar se cerro (Ctrl+D / Ctrl+Z): se sale sin error
			log.info("Entrada cerrada, saliendo de PersonApp CLI");
		}
		System.out.println("Hasta luego.");
	}

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

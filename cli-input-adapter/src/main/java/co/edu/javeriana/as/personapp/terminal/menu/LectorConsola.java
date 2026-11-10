package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lee datos de la entrada estándar línea por línea.
 *
 * Siempre usa {@code nextLine()}: mezclar {@code nextInt()} con {@code nextLine()}
 * deja el salto de línea pendiente en el búfer y la siguiente lectura llega vacía.
 * Los métodos de datos obligatorios repiten la pregunta hasta recibir un valor
 * válido. Si la entrada se cierra, {@code nextLine()} lanza
 * {@link java.util.NoSuchElementException}, que el menú principal usa para terminar.
 */
public class LectorConsola {

	/** Fuente de la entrada. */
	private final Scanner scanner;

	/**
	 * Crea el lector sobre un {@link Scanner}.
	 *
	 * @param scanner fuente de la entrada, normalmente {@code System.in}
	 */
	public LectorConsola(Scanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Lee una opción de menú.
	 *
	 * @return número de la opción
	 */
	public int leerOpcion() {
		return leerEntero("Ingrese una opcion: ");
	}

	/**
	 * Lee un número entero obligatorio; repite la pregunta mientras el valor no sea
	 * válido.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return número ingresado
	 */
	public Integer leerEntero(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			String linea = scanner.nextLine().trim();
			try {
				return Integer.valueOf(linea);
			} catch (NumberFormatException e) {
				System.out.println("  -> Solo se permiten numeros enteros.");
			}
		}
	}

	/**
	 * Lee un número entero opcional.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return número ingresado, o {@code null} si se deja vacío
	 */
	public Integer leerEnteroOpcional(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			String linea = scanner.nextLine().trim();
			if (linea.isEmpty()) {
				return null;
			}
			try {
				return Integer.valueOf(linea);
			} catch (NumberFormatException e) {
				System.out.println("  -> Escriba un numero entero o deje vacio.");
			}
		}
	}

	/**
	 * Lee un texto obligatorio; repite la pregunta mientras llegue vacío.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return texto ingresado, sin espacios en los extremos
	 */
	public String leerTexto(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			String linea = scanner.nextLine().trim();
			if (!linea.isEmpty()) {
				return linea;
			}
			System.out.println("  -> Este dato es obligatorio.");
		}
	}

	/**
	 * Lee un texto opcional.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return texto ingresado, o {@code null} si se deja vacío
	 */
	public String leerTextoOpcional(String mensaje) {
		System.out.print(mensaje);
		String linea = scanner.nextLine().trim();
		return linea.isEmpty() ? null : linea;
	}

	/**
	 * Lee el género; solo acepta M o F, sin importar mayúsculas.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return {@code M} o {@code F}
	 */
	public String leerGenero(String mensaje) {
		while (true) {
			String genero = leerTexto(mensaje).toUpperCase();
			if (genero.equals("M") || genero.equals("F")) {
				return genero;
			}
			System.out.println("  -> Escriba M o F.");
		}
	}

	/**
	 * Lee una fecha opcional con formato yyyy-MM-dd.
	 *
	 * @param mensaje texto que se muestra al usuario
	 * @return fecha ingresada, o {@code null} si se deja vacía
	 */
	public LocalDate leerFechaOpcional(String mensaje) {
		while (true) {
			String linea = leerTextoOpcional(mensaje);
			if (linea == null) {
				return null;
			}
			try {
				return LocalDate.parse(linea);
			} catch (DateTimeParseException e) {
				System.out.println("  -> Use el formato yyyy-MM-dd, por ejemplo 2024-06-15.");
			}
		}
	}
}

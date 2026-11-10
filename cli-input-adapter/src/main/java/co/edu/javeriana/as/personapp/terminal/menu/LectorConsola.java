package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lee datos del teclado linea por linea. Usar siempre nextLine() evita el
 * problema clasico de mezclar nextInt() con nextLine() (el salto de linea que
 * queda pendiente en el buffer).
 */
public class LectorConsola {

	private final Scanner scanner;

	public LectorConsola(Scanner scanner) {
		this.scanner = scanner;
	}

	public int leerOpcion() {
		return leerEntero("Ingrese una opcion: ");
	}

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

	public String leerTextoOpcional(String mensaje) {
		System.out.print(mensaje);
		String linea = scanner.nextLine().trim();
		return linea.isEmpty() ? null : linea;
	}

	public String leerGenero(String mensaje) {
		while (true) {
			String genero = leerTexto(mensaje).toUpperCase();
			if (genero.equals("M") || genero.equals("F")) {
				return genero;
			}
			System.out.println("  -> Escriba M o F.");
		}
	}

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

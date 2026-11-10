package co.edu.javeriana.as.personapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.edu.javeriana.as.personapp.terminal.menu.MenuPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class PersonAppCli implements CommandLineRunner {

	private final MenuPrincipal menuPrincipal;

	public static void main(String[] args) {
		log.info("Starting PersonAppCli ...");
		// Al salir del menu se cierra el contexto de Spring (conexiones a MariaDB y MongoDB) y termina el proceso
		System.exit(SpringApplication.exit(SpringApplication.run(PersonAppCli.class, args)));
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("EXECUTING : command line runner");
		menuPrincipal.inicio();
		log.info("FINISHED : command line runner");
	}

}

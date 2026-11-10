package co.edu.javeriana.as.personapp.application.usecase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Adaptadores de salida en memoria para las pruebas.
 *
 * Muestran la idea central de la arquitectura hexagonal: los casos de uso se
 * prueban sin MariaDB, sin MongoDB y sin Spring, porque solo dependen de los
 * puertos de salida.
 */
final class InMemoryPorts {

	/** Clase contenedora; no se instancia. */
	private InMemoryPorts() {
	}

	/** Implementación en memoria de {@link PersonOutputPort}, indexada por cédula. */
	static class Persons implements PersonOutputPort {
		/** Personas guardadas, por cédula. */
		final Map<Integer, Person> data = new LinkedHashMap<>();

		/** {@inheritDoc} */
		@Override
		public Person save(Person person) {
			data.put(person.getIdentification(), person);
			return person;
		}

		/** {@inheritDoc} */
		@Override
		public Boolean delete(Integer identification) {
			data.remove(identification);
			return !data.containsKey(identification);
		}

		/** {@inheritDoc} */
		@Override
		public List<Person> find() {
			return new ArrayList<>(data.values());
		}

		/** {@inheritDoc} */
		@Override
		public Person findById(Integer identification) {
			return data.get(identification);
		}
	}

	/** Implementación en memoria de {@link ProfessionOutputPort}, indexada por identificador. */
	static class Professions implements ProfessionOutputPort {
		/** Profesiones guardadas, por identificador. */
		final Map<Integer, Profession> data = new LinkedHashMap<>();

		/** {@inheritDoc} */
		@Override
		public Profession save(Profession profession) {
			data.put(profession.getIdentification(), profession);
			return profession;
		}

		/** {@inheritDoc} */
		@Override
		public Boolean delete(Integer identification) {
			data.remove(identification);
			return !data.containsKey(identification);
		}

		/** {@inheritDoc} */
		@Override
		public List<Profession> find() {
			return new ArrayList<>(data.values());
		}

		/** {@inheritDoc} */
		@Override
		public Profession findById(Integer identification) {
			return data.get(identification);
		}
	}

	/** Implementación en memoria de {@link PhoneOutputPort}, indexada por número. */
	static class Phones implements PhoneOutputPort {
		/** Teléfonos guardados, por número. */
		final Map<String, Phone> data = new LinkedHashMap<>();

		/** {@inheritDoc} */
		@Override
		public Phone save(Phone phone) {
			data.put(phone.getNumber(), phone);
			return phone;
		}

		/** {@inheritDoc} */
		@Override
		public Boolean delete(String number) {
			data.remove(number);
			return !data.containsKey(number);
		}

		/** {@inheritDoc} */
		@Override
		public List<Phone> find() {
			return new ArrayList<>(data.values());
		}

		/** {@inheritDoc} */
		@Override
		public Phone findById(String number) {
			return data.get(number);
		}
	}

	/** Implementación en memoria de {@link StudyOutputPort}, indexada por la llave compuesta. */
	static class Studies implements StudyOutputPort {
		/** Estudios guardados, por llave {@code cedula-idProfesion}. */
		final Map<String, Study> data = new LinkedHashMap<>();

		/**
		 * Arma la llave del mapa a partir de la llave compuesta.
		 *
		 * @param personId cédula de la persona
		 * @param professionId identificador de la profesión
		 * @return llave con la forma {@code cedula-idProfesion}
		 */
		private static String key(Integer personId, Integer professionId) {
			return personId + "-" + professionId;
		}

		/** {@inheritDoc} */
		@Override
		public Study save(Study study) {
			data.put(key(study.getPerson().getIdentification(), study.getProfession().getIdentification()), study);
			return study;
		}

		/** {@inheritDoc} */
		@Override
		public Boolean delete(Integer personIdentification, Integer professionIdentification) {
			data.remove(key(personIdentification, professionIdentification));
			return !data.containsKey(key(personIdentification, professionIdentification));
		}

		/** {@inheritDoc} */
		@Override
		public List<Study> find() {
			return new ArrayList<>(data.values());
		}

		/** {@inheritDoc} */
		@Override
		public Study findById(Integer personIdentification, Integer professionIdentification) {
			return data.get(key(personIdentification, professionIdentification));
		}
	}
}

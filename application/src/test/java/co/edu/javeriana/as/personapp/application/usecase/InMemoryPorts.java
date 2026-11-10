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
 * Adaptadores de salida "falsos" en memoria. Demuestran la idea central de la
 * arquitectura hexagonal: los casos de uso se prueban sin MariaDB, sin MongoDB
 * y sin Spring, porque solo dependen de los puertos (interfaces).
 */
final class InMemoryPorts {

	private InMemoryPorts() {
	}

	static class Persons implements PersonOutputPort {
		final Map<Integer, Person> data = new LinkedHashMap<>();

		@Override
		public Person save(Person person) {
			data.put(person.getIdentification(), person);
			return person;
		}

		@Override
		public Boolean delete(Integer identification) {
			data.remove(identification);
			return !data.containsKey(identification);
		}

		@Override
		public List<Person> find() {
			return new ArrayList<>(data.values());
		}

		@Override
		public Person findById(Integer identification) {
			return data.get(identification);
		}
	}

	static class Professions implements ProfessionOutputPort {
		final Map<Integer, Profession> data = new LinkedHashMap<>();

		@Override
		public Profession save(Profession profession) {
			data.put(profession.getIdentification(), profession);
			return profession;
		}

		@Override
		public Boolean delete(Integer identification) {
			data.remove(identification);
			return !data.containsKey(identification);
		}

		@Override
		public List<Profession> find() {
			return new ArrayList<>(data.values());
		}

		@Override
		public Profession findById(Integer identification) {
			return data.get(identification);
		}
	}

	static class Phones implements PhoneOutputPort {
		final Map<String, Phone> data = new LinkedHashMap<>();

		@Override
		public Phone save(Phone phone) {
			data.put(phone.getNumber(), phone);
			return phone;
		}

		@Override
		public Boolean delete(String number) {
			data.remove(number);
			return !data.containsKey(number);
		}

		@Override
		public List<Phone> find() {
			return new ArrayList<>(data.values());
		}

		@Override
		public Phone findById(String number) {
			return data.get(number);
		}
	}

	static class Studies implements StudyOutputPort {
		final Map<String, Study> data = new LinkedHashMap<>();

		private static String key(Integer personId, Integer professionId) {
			return personId + "-" + professionId;
		}

		@Override
		public Study save(Study study) {
			data.put(key(study.getPerson().getIdentification(), study.getProfession().getIdentification()), study);
			return study;
		}

		@Override
		public Boolean delete(Integer personIdentification, Integer professionIdentification) {
			data.remove(key(personIdentification, professionIdentification));
			return !data.containsKey(key(personIdentification, professionIdentification));
		}

		@Override
		public List<Study> find() {
			return new ArrayList<>(data.values());
		}

		@Override
		public Study findById(Integer personIdentification, Integer professionIdentification) {
			return data.get(key(personIdentification, professionIdentification));
		}
	}
}

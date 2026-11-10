package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

/**
 * Entidad JPA de la tabla {@code persona}.
 *
 * Los teléfonos y estudios usan {@code cascade = ALL}: al eliminar una persona
 * también se eliminan sus registros dependientes.
 *
 * @author aasanchez
 */
@Entity
@Table(name="persona", catalog = "persona_db")
@NamedQueries({ @NamedQuery(name = "PersonaEntity.findAll", query = "SELECT p FROM PersonaEntity p"),
		@NamedQuery(name = "PersonaEntity.findByCc", query = "SELECT p FROM PersonaEntity p WHERE p.cc = :cc"),
		@NamedQuery(name = "PersonaEntity.findByNombre", query = "SELECT p FROM PersonaEntity p WHERE p.nombre = :nombre"),
		@NamedQuery(name = "PersonaEntity.findByApellido", query = "SELECT p FROM PersonaEntity p WHERE p.apellido = :apellido"),
		@NamedQuery(name = "PersonaEntity.findByGenero", query = "SELECT p FROM PersonaEntity p WHERE p.genero = :genero"),
		@NamedQuery(name = "PersonaEntity.findByEdad", query = "SELECT p FROM PersonaEntity p WHERE p.edad = :edad") })
public class PersonaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Cédula, llave primaria. */
	@Id
	@Basic(optional = false)
	@Column(nullable = false)
	private Integer cc;

	/** Nombre, máximo 45 caracteres. */
	@Basic(optional = false)
	@Column(nullable = false, length = 45)
	private String nombre;

	/** Apellido, máximo 45 caracteres. */
	@Basic(optional = false)
	@Column(nullable = false, length = 45)
	private String apellido;

	/** Género: {@code M} o {@code F} (columna ENUM). */
	@Basic(optional = false)
	@Column(nullable = false)
	private Character genero;

	/** Edad; puede ser nula. */
	private Integer edad;

	/** Teléfonos de la persona. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "duenio")
	private List<TelefonoEntity> telefonos;

	/** Estudios de la persona. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "persona")
	private List<EstudiosEntity> estudios;

	/** Constructor vacío requerido por JPA. */
	public PersonaEntity() {
	}

	/**
	 * Crea una referencia a la persona con solo su cédula.
	 *
	 * @param cc cédula de la persona
	 */
	public PersonaEntity(Integer cc) {
		this.cc = cc;
	}

	/**
	 * Crea la persona con sus datos obligatorios.
	 *
	 * @param cc cédula
	 * @param nombre nombre
	 * @param apellido apellido
	 * @param genero género, {@code M} o {@code F}
	 */
	public PersonaEntity(Integer cc, String nombre, String apellido, Character genero) {
		this.cc = cc;
		this.nombre = nombre;
		this.apellido = apellido;
		this.genero = genero;
	}

	/** @return cédula */
	public Integer getCc() {
		return cc;
	}

	/** @param cc cédula */
	public void setCc(Integer cc) {
		this.cc = cc;
	}

	/** @return nombre */
	public String getNombre() {
		return nombre;
	}

	/** @param nombre nombre */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/** @return apellido */
	public String getApellido() {
		return apellido;
	}

	/** @param apellido apellido */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/** @return género, {@code M} o {@code F} */
	public Character getGenero() {
		return genero;
	}

	/** @param genero género, {@code M} o {@code F} */
	public void setGenero(Character genero) {
		this.genero = genero;
	}

	/** @return edad; puede ser nula */
	public Integer getEdad() {
		return edad;
	}

	/** @param edad edad en años */
	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	/** @return teléfonos de la persona */
	public List<TelefonoEntity> getTelefonos() {
		return telefonos;
	}

	/** @param telefonoList teléfonos de la persona */
	public void setTelefonos(List<TelefonoEntity> telefonoList) {
		this.telefonos = telefonoList;
	}

	/** @return estudios de la persona */
	public List<EstudiosEntity> getEstudios() {
		return estudios;
	}

	/** @param estudiosList estudios de la persona */
	public void setEstudios(List<EstudiosEntity> estudiosList) {
		this.estudios = estudiosList;
	}

	/**
	 * Calcula el hash a partir de la cédula.
	 *
	 * @return hash de la persona
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (cc != null ? cc.hashCode() : 0);
		return hash;
	}

	/**
	 * Compara dos registros por la cédula.
	 *
	 * @param object objeto a comparar
	 * @return {@code true} si el objeto es del mismo tipo y tiene la misma cédula
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PersonaEntity)) {
			return false;
		}
		PersonaEntity other = (PersonaEntity) object;
		if ((this.cc == null && other.cc != null) || (this.cc != null && !this.cc.equals(other.cc))) {
			return false;
		}
		return true;
	}

	/** @return representación de la persona sin sus relaciones */
	@Override
	public String toString() {
		return "PersonaEntity [cc=" + cc + ", nombre=" + nombre + ", apellido=" + apellido + ", genero=" + genero + ", edad="
				+ edad + "]";
	}

}

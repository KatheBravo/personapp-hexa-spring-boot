package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Entidad JPA de la tabla {@code estudios}. La llave primaria es compuesta
 * (id_prof, cc_per) y se representa con {@link EstudiosEntityPK}.
 *
 * @author aasanchez
 */
@Entity
@Table(name="estudios", catalog = "persona_db")
@NamedQueries({ @NamedQuery(name = "EstudiosEntity.findAll", query = "SELECT e FROM EstudiosEntity e"),
		@NamedQuery(name = "EstudiosEntity.findByIdProf", query = "SELECT e FROM EstudiosEntity e WHERE e.estudiosEntityPK.idProf = :idProf"),
		@NamedQuery(name = "EstudiosEntity.findByCcPer", query = "SELECT e FROM EstudiosEntity e WHERE e.estudiosEntityPK.ccPer = :ccPer"),
		@NamedQuery(name = "EstudiosEntity.findByFecha", query = "SELECT e FROM EstudiosEntity e WHERE e.fecha = :fecha"),
		@NamedQuery(name = "EstudiosEntity.findByUniver", query = "SELECT e FROM EstudiosEntity e WHERE e.univer = :univer") })
public class EstudiosEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Llave compuesta: identificador de la profesión y cédula de la persona. */
	@EmbeddedId
	protected EstudiosEntityPK estudiosEntityPK;

	/** Fecha de grado (DATE). Hibernate mapea {@code LocalDate} sin {@code @Temporal}. */
	@Column(name = "fecha")
	private LocalDate fecha;

	/** Universidad, máximo 50 caracteres. */
	@Column(length = 50)
	private String univer;

	/** Persona del estudio; la columna cc_per se escribe a través de la llave. */
	@JoinColumn(name = "cc_per", referencedColumnName = "cc", nullable = false, insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private PersonaEntity persona;

	/** Profesión del estudio; la columna id_prof se escribe a través de la llave. */
	@JoinColumn(name = "id_prof", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private ProfesionEntity profesion;

	/** Constructor vacío requerido por JPA. */
	public EstudiosEntity() {
	}

	/**
	 * Crea el estudio con su llave.
	 *
	 * @param estudiosEntityPK llave compuesta del estudio
	 */
	public EstudiosEntity(EstudiosEntityPK estudiosEntityPK) {
		this.estudiosEntityPK = estudiosEntityPK;
	}

	/**
	 * Crea el estudio a partir de los valores de la llave.
	 *
	 * @param idProf identificador de la profesión
	 * @param ccPer cédula de la persona
	 */
	public EstudiosEntity(int idProf, int ccPer) {
		this.estudiosEntityPK = new EstudiosEntityPK(idProf, ccPer);
	}

	/** @return llave compuesta del estudio */
	public EstudiosEntityPK getEstudiosPK() {
		return estudiosEntityPK;
	}

	/** @param estudiosEntityPK llave compuesta del estudio */
	public void setEstudiosPK(EstudiosEntityPK estudiosEntityPK) {
		this.estudiosEntityPK = estudiosEntityPK;
	}

	/** @return fecha de grado; puede ser nula */
	public LocalDate getFecha() {
		return fecha;
	}

	/** @param fecha fecha de grado */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/** @return universidad */
	public String getUniver() {
		return univer;
	}

	/** @param univer universidad */
	public void setUniver(String univer) {
		this.univer = univer;
	}

	/** @return persona del estudio */
	public PersonaEntity getPersona() {
		return persona;
	}

	/** @param personaEntity persona del estudio */
	public void setPersona(PersonaEntity personaEntity) {
		this.persona = personaEntity;
	}

	/** @return profesión del estudio */
	public ProfesionEntity getProfesion() {
		return profesion;
	}

	/** @param profesionEntity profesión del estudio */
	public void setProfesion(ProfesionEntity profesionEntity) {
		this.profesion = profesionEntity;
	}

	/**
	 * Calcula el hash a partir de la llave compuesta.
	 *
	 * @return hash del estudio
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (estudiosEntityPK != null ? estudiosEntityPK.hashCode() : 0);
		return hash;
	}

	/**
	 * Compara por la llave compuesta.
	 *
	 * @param object objeto a comparar
	 * @return {@code true} si el objeto es del mismo tipo y tiene la misma llave compuesta
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EstudiosEntity)) {
			return false;
		}
		EstudiosEntity other = (EstudiosEntity) object;
		if ((this.estudiosEntityPK == null && other.estudiosEntityPK != null)
				|| (this.estudiosEntityPK != null && !this.estudiosEntityPK.equals(other.estudiosEntityPK))) {
			return false;
		}
		return true;
	}

	/** @return representación del estudio sin sus relaciones */
	@Override
	public String toString() {
		return "EstudiosEntity [estudiosEntityPK=" + estudiosEntityPK + ", fecha=" + fecha + ", univer=" + univer + "]";
	}

}

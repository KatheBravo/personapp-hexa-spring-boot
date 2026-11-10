package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA de la tabla {@code profesion}. Al eliminar una profesión también
 * se eliminan sus estudios ({@code cascade = ALL}).
 *
 * @author aasanchez
 */
@Entity
@Table(name="profesion", catalog = "persona_db")
@NamedQueries({ @NamedQuery(name = "ProfesionEntity.findAll", query = "SELECT p FROM ProfesionEntity p"),
		@NamedQuery(name = "ProfesionEntity.findById", query = "SELECT p FROM ProfesionEntity p WHERE p.id = :id"),
		@NamedQuery(name = "ProfesionEntity.findByNom", query = "SELECT p FROM ProfesionEntity p WHERE p.nom = :nom") })
public class ProfesionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador, llave primaria. */
	@Id
	@Basic(optional = false)
	@Column(nullable = false)
	private Integer id;

	/** Nombre, máximo 90 caracteres. */
	@Basic(optional = false)
	@Column(nullable = false, length = 90)
	private String nom;

	/** Descripción. Se declara como TEXT en lugar de {@code @Lob} para que Hibernate no la lea como CLOB. */
	@Column(columnDefinition = "TEXT")
	private String des;

	/** Estudios registrados en la profesión. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "profesion")
	private List<EstudiosEntity> estudios;

	/** Constructor vacío requerido por JPA. */
	public ProfesionEntity() {
	}

	/**
	 * Crea una referencia a la profesión con solo su identificador.
	 *
	 * @param id identificador de la profesión
	 */
	public ProfesionEntity(Integer id) {
		this.id = id;
	}

	/**
	 * Crea la profesión con sus datos obligatorios.
	 *
	 * @param id identificador
	 * @param nom nombre
	 */
	public ProfesionEntity(Integer id, String nom) {
		this.id = id;
		this.nom = nom;
	}

	/** @return identificador */
	public Integer getId() {
		return id;
	}

	/** @param id identificador */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @return nombre */
	public String getNom() {
		return nom;
	}

	/** @param nom nombre */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/** @return descripción */
	public String getDes() {
		return des;
	}

	/** @param des descripción */
	public void setDes(String des) {
		this.des = des;
	}

	/** @return estudios de la profesión */
	public List<EstudiosEntity> getEstudios() {
		return estudios;
	}

	/** @param estudiosList estudios de la profesión */
	public void setEstudios(List<EstudiosEntity> estudiosList) {
		this.estudios = estudiosList;
	}

	/**
	 * Calcula el hash a partir de el identificador.
	 *
	 * @return hash de la profesión
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	/**
	 * Compara dos registros por el identificador.
	 *
	 * @param object objeto a comparar
	 * @return {@code true} si el objeto es del mismo tipo y tiene el mismo identificador
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ProfesionEntity)) {
			return false;
		}
		ProfesionEntity other = (ProfesionEntity) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	/** @return representación de la profesión sin sus relaciones */
	@Override
	public String toString() {
		return "ProfesionEntity [id=" + id + ", nom=" + nom + ", des=" + des + "]";
	}

}

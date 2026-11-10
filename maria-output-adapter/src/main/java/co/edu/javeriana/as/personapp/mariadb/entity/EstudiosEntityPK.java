package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Llave primaria compuesta de la tabla {@code estudios}: identificador de la
 * profesión y cédula de la persona.
 *
 * @author aasanchez
 */
@Embeddable
public class EstudiosEntityPK implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador de la profesión (columna id_prof). */
	@Basic(optional = false)
	@Column(name = "id_prof", nullable = false)
	private int idProf;

	/** Cédula de la persona (columna cc_per). */
	@Basic(optional = false)
	@Column(name = "cc_per", nullable = false)
	private int ccPer;

	/** Constructor vacío requerido por JPA. */
	public EstudiosEntityPK() {
	}

	/**
	 * Crea la llave con sus dos valores.
	 *
	 * @param idProf identificador de la profesión
	 * @param ccPer cédula de la persona
	 */
	public EstudiosEntityPK(int idProf, int ccPer) {
		this.idProf = idProf;
		this.ccPer = ccPer;
	}

	/** @return identificador de la profesión */
	public int getIdProf() {
		return idProf;
	}

	/** @param idProf identificador de la profesión */
	public void setIdProf(int idProf) {
		this.idProf = idProf;
	}

	/** @return cédula de la persona */
	public int getCcPer() {
		return ccPer;
	}

	/** @param ccPer cédula de la persona */
	public void setCcPer(int ccPer) {
		this.ccPer = ccPer;
	}

	/**
	 * Calcula el hash a partir de los dos campos de la llave.
	 *
	 * @return hash de la llave
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (int) idProf;
		hash += (int) ccPer;
		return hash;
	}

	/**
	 * Compara dos llaves campo por campo.
	 *
	 * @param object objeto a comparar
	 * @return {@code true} si el objeto es una llave con los mismos valores
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EstudiosEntityPK)) {
			return false;
		}
		EstudiosEntityPK other = (EstudiosEntityPK) object;
		if (this.idProf != other.idProf) {
			return false;
		}
		if (this.ccPer != other.ccPer) {
			return false;
		}
		return true;
	}

	/** @return representación de la llave */
	@Override
	public String toString() {
		return "EstudiosEntityPK [idProf=" + idProf + ", ccPer=" + ccPer + "]";
	}

}

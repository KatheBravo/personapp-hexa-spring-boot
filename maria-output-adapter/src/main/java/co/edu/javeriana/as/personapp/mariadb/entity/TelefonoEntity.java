package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Entidad JPA de la tabla {@code telefono}. La columna {@code duenio} es llave
 * foránea a la cédula de la persona.
 *
 * @author aasanchez
 */
@Entity
@Table(name="telefono", catalog = "persona_db")
@NamedQueries({ @NamedQuery(name = "TelefonoEntity.findAll", query = "SELECT t FROM TelefonoEntity t"),
		@NamedQuery(name = "TelefonoEntity.findByNum", query = "SELECT t FROM TelefonoEntity t WHERE t.num = :num"),
		@NamedQuery(name = "TelefonoEntity.findByOper", query = "SELECT t FROM TelefonoEntity t WHERE t.oper = :oper") })
public class TelefonoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Número, llave primaria, máximo 15 caracteres. */
	@Id
	@Basic(optional = false)
	@Column(nullable = false, length = 15)
	private String num;

	/** Operador, máximo 45 caracteres. */
	@Basic(optional = false)
	@Column(nullable = false, length = 45)
	private String oper;

	/** Persona dueña del teléfono. */
	@JoinColumn(name = "duenio", referencedColumnName = "cc", nullable = false)
	@ManyToOne(optional = false)
	private PersonaEntity duenio;

	/** Constructor vacío requerido por JPA. */
	public TelefonoEntity() {
	}

	/**
	 * Crea una referencia al teléfono con solo su número.
	 *
	 * @param num número del teléfono
	 */
	public TelefonoEntity(String num) {
		this.num = num;
	}

	/**
	 * Crea el teléfono con número y operador.
	 *
	 * @param num número
	 * @param oper operador
	 */
	public TelefonoEntity(String num, String oper) {
		this.num = num;
		this.oper = oper;
	}

	/** @return número */
	public String getNum() {
		return num;
	}

	/** @param num número */
	public void setNum(String num) {
		this.num = num;
	}

	/** @return operador */
	public String getOper() {
		return oper;
	}

	/** @param oper operador */
	public void setOper(String oper) {
		this.oper = oper;
	}

	/** @return persona dueña */
	public PersonaEntity getDuenio() {
		return duenio;
	}

	/** @param duenio persona dueña */
	public void setDuenio(PersonaEntity duenio) {
		this.duenio = duenio;
	}

	/**
	 * Calcula el hash a partir de el número.
	 *
	 * @return hash del teléfono
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (num != null ? num.hashCode() : 0);
		return hash;
	}

	/**
	 * Compara dos registros por el número.
	 *
	 * @param object objeto a comparar
	 * @return {@code true} si el objeto es del mismo tipo y tiene el mismo número
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TelefonoEntity)) {
			return false;
		}
		TelefonoEntity other = (TelefonoEntity) object;
		if ((this.num == null && other.num != null) || (this.num != null && !this.num.equals(other.num))) {
			return false;
		}
		return true;
	}

	/** @return representación del teléfono sin sus relaciones */
	@Override
	public String toString() {
		return "TelefonoEntity [num=" + num + ", oper=" + oper + "]";
	}

}

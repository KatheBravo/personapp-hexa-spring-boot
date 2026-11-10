package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marca una interfaz como puerto de la capa de aplicación.
 *
 * Los adaptadores de entrada usan los puertos de entrada y los adaptadores de
 * salida implementan los puertos de salida.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Port {

	/**
	 * Nombre descriptivo del puerto.
	 *
	 * @return nombre del puerto; vacío por defecto
	 */
	String value() default "";
}

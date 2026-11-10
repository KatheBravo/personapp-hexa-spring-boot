package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Marca una clase como caso de uso: implementa un puerto de entrada y contiene
 * las reglas de negocio de la aplicación.
 *
 * Es un alias de {@link Component}.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface UseCase {
	
    /**
     * Nombre del bean. Se usa con {@code @Qualifier} cuando hay varios beans del mismo tipo.
     *
     * @return nombre del bean; vacío para usar el nombre por defecto
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}

package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Marca una clase como adaptador de la arquitectura hexagonal, de entrada o de salida.
 *
 * Es un alias de {@link Component}: Spring registra la clase como bean sin que
 * los adaptadores dependan directamente de las anotaciones del framework.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface Adapter {
	
    /**
     * Nombre del bean. Se usa con {@code @Qualifier} cuando hay varios beans del mismo tipo.
     *
     * @return nombre del bean; vacío para usar el nombre por defecto
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}

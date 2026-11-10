package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Marca una clase que convierte objetos entre el dominio y el modelo de un
 * adaptador: entidades JPA, documentos de MongoDB, DTO de REST o modelos de consola.
 *
 * Es un alias de {@link Component}.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface Mapper {
	
    /**
     * Nombre del bean. Se usa con {@code @Qualifier} cuando hay varios beans del mismo tipo.
     *
     * @return nombre del bean; vacío para usar el nombre por defecto
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}

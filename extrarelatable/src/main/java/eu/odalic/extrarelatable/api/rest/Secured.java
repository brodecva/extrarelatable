package eu.odalic.extrarelatable.api.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import eu.odalic.extrarelatable.api.rest.util.Role;

/**
 * Secured resource annotation. Used to annotate resources where the access
 * restriction has to be applied.
 *
 * @author Václav Brodec
 *
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Secured {
	Role[] value() default {};
}

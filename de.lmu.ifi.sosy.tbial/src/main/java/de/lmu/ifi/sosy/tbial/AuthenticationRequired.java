package de.lmu.ifi.sosy.tbial;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.apache.wicket.markup.html.WebPage;

/**
 * Indicates that the {@link WebPage} requires a valid, signed-in user.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 * @see TBIALApplication#initAuthorization
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface AuthenticationRequired {}

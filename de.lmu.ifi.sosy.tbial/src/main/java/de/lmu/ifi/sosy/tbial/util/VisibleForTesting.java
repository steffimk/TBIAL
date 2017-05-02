package de.lmu.ifi.sosy.tbial.util;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the visibility of the class, constructor or method was increased to ease testing
 * of the class.
 *
 * @author SWEP Team 2013
 */
@Target({TYPE, CONSTRUCTOR, METHOD})
@Retention(SOURCE)
public @interface VisibleForTesting {}

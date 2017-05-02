package de.lmu.ifi.sosy.tbial;

/**
 * Exception that indicates that an exception was encountered while accessing the (SQL) database.
 *
 * @author Christian Kroiß, SWEP 2013 Team.
 */
public class DatabaseException extends RuntimeException {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}

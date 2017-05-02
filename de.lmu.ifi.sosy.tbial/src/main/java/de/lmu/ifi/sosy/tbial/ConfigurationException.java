package de.lmu.ifi.sosy.tbial;

/**
 * Exception that indicates that the system was not properly configured.
 *
 * @author Christian Kroiß, SWEP 2013 Team.
 */
public class ConfigurationException extends RuntimeException {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public ConfigurationException(String message) {
    this(message, null);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}

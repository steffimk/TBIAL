package de.lmu.ifi.sosy.tbial;

/**
 * Categories of error codes. Each category encapsulates a numeric error code that can be displayed
 * on the error page. This allows the user to refer to encountered problems without the need to
 * reveal detailed technical error information.
 *
 * @author Christian Kroiß, SWEP 2013 Team.
 */
public enum ErrorCategory {
  GeneralError(0),
  DatabaseError(1),
  ConfigurationError(2);

  private int errorCode;

  private ErrorCategory(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }

  @Override
  public String toString() {
    return name() + "(" + errorCode + ")";
  }
}

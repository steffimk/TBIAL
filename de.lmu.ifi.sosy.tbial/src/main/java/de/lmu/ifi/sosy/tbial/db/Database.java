package de.lmu.ifi.sosy.tbial.db;

/**
 * The interface offered by the database (or, more precisely, the data access layer).
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public interface Database {

  /**
   * Retrieves the user with the given name.
   *
   * @param name should not be null.
   * @return the user with the given name.
   */
  User getUser(String name);

  /**
   * Returns whether a user with the given name exits.
   *
   * @param name should not be null.
   * @return {@code true} if the name is already taken, {@code false} otherwise.
   */
  boolean userNameTaken(String name);

  /**
   * Registers a new user with the given name and password.
   *
   * @param name should not be null
   * @param password should not be null
   * @return a new user object or {@code null}, if a user with the given name already exists in the
   *     database.
   */
  User register(String name, String password);

}

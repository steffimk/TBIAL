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
  boolean nameTaken(String name);

  /**
   * Registers a new user with the given name and password.
   *
   * @param name should not be null
   * @param password should not be null
   * @return a new user object or {@code null}, if a user with the given name already exists in the
   *     database.
   */
  User register(String name, String password);

  /**
   * Creates a new game with the given name, maxPlayers, privacy setting and respectively password.
   *
   * @param hostId is id of the user who creates the game and should not be null
   * @param name should not be null
   * @param maxPlayers should not be null
   * @param isPrivate specifies whether the game is password protected
   * @param password should not be null if isPrivate equals true
   * @return a new game object or {@code null}, if a game with the given name already exists in the
   *     database.
   */
  Game newGame(int hostId, String name, int maxPlayers, boolean isPrivate, String password);
}

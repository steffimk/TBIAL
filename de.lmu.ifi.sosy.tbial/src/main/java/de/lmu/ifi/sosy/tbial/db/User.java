package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/**
 * A user with a user name and a plain-text password.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class User implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private int id = -1;

  private String name;

  private String password;

  public User(String name, String password) {
    this(-1, name, password);
  }

  public User(int id, String name, String password) {
    this.id = id;
    this.name = requireNonNull(name);
    this.password = requireNonNull(password);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = requireNonNull(name);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = requireNonNull(password);
  }

  /**
   * Returns the database id of the user. This id is only used for persistence and is only set when
   * a user object is written or read form the database. The id is not included in {@link
   * #equals(Object)} and {@link #hashCode()} .
   *
   * @return the database id of the user.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the user's persistence id. This is package private so only database implementations can
   * use it.
   *
   * @param id the user's persistence id.
   */
  void setId(int id) {
    this.id = id;
  }
}

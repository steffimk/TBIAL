package de.lmu.ifi.sosy.tbial.db;

import java.io.Serializable;
import static java.util.Objects.requireNonNull;

/** A game. */
public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private int id = -1;

  private int hostId;

  private String name;

  private int maxPlayers;

  private boolean isPrivate;

  private String password = "";

  public Game(int id, int hostId, String name, int maxPlayers, boolean isPrivate, String password) {
    this.id = id;
    this.hostId = requireNonNull(hostId);
    this.name = requireNonNull(name);
    this.maxPlayers = requireNonNull(maxPlayers);
    this.isPrivate = isPrivate;
    
    if (isPrivate) {
      this.password = requireNonNull(password);
    }
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

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  /**
   * Returns the database id of the game. This id is only used for persistence and is only set when
   * a user object is written or read form the database.
   *
   * @return the database id of the game.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the games's persistence id. This is package private so only database implementations can
   * use it.
   *
   * @param id the game's persistence id.
   */
  void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Game(Id:"
        + id
        + ", Name: "
        + name
        + ", maxPlayers: "
        + maxPlayers
        + ", isPrivate: "
        + isPrivate
        + ")";
  }
}

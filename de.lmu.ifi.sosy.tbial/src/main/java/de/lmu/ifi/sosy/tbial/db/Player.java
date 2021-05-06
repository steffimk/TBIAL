package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/** A player with a userId, gameId and isHost-field */
public class Player implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private int id;

  private int userId;

  private int gameId;

  private boolean isHost;

  public Player(int id, int userId, int gameId, boolean isHost) {
    this.gameId = id;
    this.userId = requireNonNull(userId);
    this.gameId = requireNonNull(gameId);
    this.isHost = requireNonNull(isHost);
  }
}

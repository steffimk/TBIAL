package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;

/**
 * A player of a game.
 *
 * <p>Contains all information of the player (cards, mental health,...)
 */
public class Player implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private final String userName;
  // private int mentalHealth;

  public Player(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }
}

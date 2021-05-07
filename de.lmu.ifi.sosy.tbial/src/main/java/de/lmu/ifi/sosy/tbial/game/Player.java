package de.lmu.ifi.sosy.tbial.game;

/**
 * A player of a game.
 *
 * <p>Contains all information of the player (cards, mental health,...)
 */
public class Player {

  private final String userName;
  // private int mentalHealth;

  public Player(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }
}

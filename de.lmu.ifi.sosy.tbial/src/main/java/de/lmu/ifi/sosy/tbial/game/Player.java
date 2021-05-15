package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A player of a game.
 *
 * <p>Contains all information of the player (cards, mental health,...)
 */
public class Player implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private final String userName;
  private int mentalHealth;
  private int prestige;
  private int bug;
  private RoleCard role;
  private CharacterCard character;
  private List<StackCard> handCards;
  private List<StackCard> playedCards;
  private List<StackCard> stumblingBlockCards;

  public Player(String userName) {
    this.userName = userName;
    this.mentalHealth = 0;
    this.prestige = 0;
    this.bug = 0;
    this.role = null;
    this.character = null;
    this.handCards = new ArrayList<StackCard>();
    this.playedCards = new ArrayList<StackCard>();
    this.stumblingBlockCards = new ArrayList<StackCard>();
  }

  public String getUserName() {
    return userName;
  }

  public String getMentalHealth() {
    return "Mental Health: " + mentalHealth;
  }

  public String getPrestige() {
    return "Prestige: " + prestige;
  }

  public String getBug() {
    return "Bug: " + bug;
  }

  public RoleCard getRole() {
    return role;
  }

  public CharacterCard getCharacter() {
    return character;
  }

  public List<StackCard> getHandCards() {
    return handCards;
  }

  public List<StackCard> getPlayedCards() {
    return playedCards;
  }

  public List<StackCard> getStumblingBlockCards() {
    return stumblingBlockCards;
  }
}

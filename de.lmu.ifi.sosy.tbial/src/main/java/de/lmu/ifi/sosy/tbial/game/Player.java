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
  private String role;
  private String character;
  // update all to StackCard when branch is merged into master
  private List<String> handCards;
  private List<String> playedCards;
  private List<String> stumblingBlockCards;

  public Player(String userName) {
    this.userName = userName;
    this.mentalHealth = 0;
    this.prestige = 0;
    this.bug = 0;
    this.role = "";
    this.character = "";
    this.handCards = new ArrayList<String>();
    this.playedCards = new ArrayList<String>();
    this.stumblingBlockCards = new ArrayList<String>();
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

  public String getRole() {
    return role;
  }

  public String getCharacter() {
    return character;
  }

  public List<String> getHandCards() {
    return handCards;
  }

  public List<String> getPlayedCards() {
    return playedCards;
  }

  public List<String> getStumblingBlockCards() {
    return stumblingBlockCards;
  }
  
  public void setMentalHealth(int m) {
	  this.mentalHealth=m;
  }
  
}

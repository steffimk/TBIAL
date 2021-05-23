package de.lmu.ifi.sosy.tbial.game;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A player of a game.
 *
 * <p>Contains all information of the player (cards, mental health,...)
 */
public class Player implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private final String userName;

  private RoleCard roleCard;
  private CharacterCard characterCard;

  private int mentalHealth;
  private int prestige;
  private int bug;

  private Set<StackCard> handCards;
  private Set<StackCard> receivedCards;

  private boolean fired;

  private boolean basePlayer;

  public Player(String userName) {
    this.userName = userName;
    this.prestige = 0;
    this.fired = false;
    this.mentalHealth = 0;
    this.bug = 0;
    this.handCards = new HashSet<>();
    this.receivedCards = new HashSet<>();
    receivedCards.add(new AbilityCard(Ability.SUNGLASSES));
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
  public RoleCard getRoleCard() {
    return roleCard;
  }

  public void setRoleCard(RoleCard roleCard) {
    this.roleCard = roleCard;
  }

  public Role getRole() {
    return roleCard.getRole();
  }

  public String getRoleName() {
    return roleCard.getLabel();
  }

  public CharacterCard getCharacterCard() {
    return characterCard;
  }

  public Set<StackCard> getHandCards() {
    return handCards;
  }

  public boolean isFired() {
    return fired;
  }

  public boolean isBasePlayer() {
    return basePlayer;
  }

  public void setCharacterCard(CharacterCard characterCard) {
    this.characterCard = characterCard;
  }

  public void fire(boolean fired) {
    this.fired = fired;
  }

  public void setBasePlayer(boolean basePlayer) {
    this.basePlayer = basePlayer;
  }

  /**
   * Sets the initial number of mental health points based on the player's character and role card.
   */
  public void initialMentalHealth() {
    requireNonNull(characterCard);
    requireNonNull(roleCard);
    mentalHealth = characterCard.getMaxHealthPoints();
    if (roleCard.getRole() == Role.MANAGER) {
      mentalHealth += 1;
    }
  }

  public int getMentalHealthInt() {
    return mentalHealth;
  }

  public int getPrestigeInt() {
    return prestige;
  }

  /**
   * Adds the value to the mental health points of the player. Use a negative value to decrement the
   * mental health.
   *
   * @param value that gets added to the mental health
   */
  public synchronized void addToMentalHealth(int value) {
    mentalHealth += value;
  }

  /**
   * Adds all cards to the player's hand cards
   *
   * @param cards - the cards to be added
   */
  public void addToHandCards(Set<StackCard> cards) {
    handCards.addAll(cards);
  }

  /**
   * Removal of a hand card. Removes the card if it is contained in this player's hand cards.
   *
   * @param card The card to be removed from the hand cards.
   * @return <code>true</code> if the removal was successful, <code>false</code> otherwise
   */
  public boolean removeHandCard(StackCard card) {
    return handCards.remove(card);
  }

  /**
   * Adds this card to the set of received cards.
   *
   * @param card The card the player is receiving.
   */
  public void receiveCard(StackCard card) {
    this.receivedCards.add(card);
  }

  public Set<StackCard> getReceivedCards() {
    return receivedCards;
  }
}

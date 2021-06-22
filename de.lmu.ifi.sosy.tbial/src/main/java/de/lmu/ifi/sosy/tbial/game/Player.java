package de.lmu.ifi.sosy.tbial.game;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import static java.util.Objects.requireNonNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

  // The different set of cards
  private Set<StackCard> handCards;
  /** The ability cards the player played. */
  private Set<AbilityCard> playedAbilityCards;
  
  private Set<StackCard> receivedCards;

  /** The last card the player has clicked on. Is <code>null</code> if no card is selected. */
  private StackCard selectedHandCard;

  private boolean fired;

  private boolean won;

  public Player(String userName) {
    this.userName = userName;
    this.prestige = 0;
    this.fired = false;
    this.mentalHealth = 0;
    this.handCards = Collections.synchronizedSet(new HashSet<>());
    this.playedAbilityCards = Collections.synchronizedSet(new HashSet<>());
    this.receivedCards = Collections.synchronizedSet(new HashSet<>());
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

  public boolean hasWon() {
    return won;
  }

  public void setCharacterCard(CharacterCard characterCard) {
    this.characterCard = characterCard;
  }

  public void setMentalHealth(int mentalHealth) {
    this.mentalHealth = mentalHealth;
  }

  public void fire(boolean fired) {
    this.fired = fired;
  }

  public void win(boolean win) {
    this.won = win;
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
   * Adds a card to the player's hand cards
   *
   * @param card - the card to be added
   */
  public void addToHandCards(StackCard card) {
    handCards.add(card);
  }

  /**
   * Removal of a hand card. Removes the card if it is contained in this player's hand cards.
   *
   * @param card The card to be removed from the hand cards.
   * @return <code>true</code> if the removal was successful, <code>false</code> otherwise
   */
  public boolean removeHandCard(StackCard card) {
    if (selectedHandCard == card) {
      selectedHandCard = null;
    }
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

  public StackCard getSelectedHandCard() {
    return selectedHandCard;
  }

  /**
   * Call when player clicked on one of his own hand cards
   *
   * @param selectedCard The card the player clicked on
   */
  public void setSelectedHandCard(StackCard selectedCard) {
    this.selectedHandCard = selectedCard;
  }

  /**
   * Adds this card to the set of uncovered cards that lay in front of the player.
   *
   * @param card The card the player wants to uncover.
   */
  public void addPlayedAbilityCard(AbilityCard card) {
    this.playedAbilityCards.add(card);
    this.selectedHandCard = null;
  }

  public Set<AbilityCard> getPlayedAbilityCards() {
    return playedAbilityCards;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof Player)) {
      return false;
    }
    Player other = (Player) o;
    return userName.equals(other.userName);
  }

  /**
   * Checks whether a player can end his turn.
   *
   * @return <code>true</code> if the turn can be ended <code>false</code> otherwise
   */
  public boolean canEndTurn() {
    return mentalHealth >= handCards.size();
  }

  /**
   * Checks whether player has the bug delegation card. If he does, there's a 25% chance this method
   * returns true.
   *
   * @return <code>true</code> if the bug gets blocked and <code>false</code> otherwise
   */
  public boolean bugGetsBlockedByBugDelegationCard() {
    Stream<AbilityCard> bugDelCards =
        playedAbilityCards.stream().filter(card -> card.getAbility() == Ability.BUG_DELEGATION);
    return bugDelCards.count() > 0 && Math.random() < 0.25;
  }
}

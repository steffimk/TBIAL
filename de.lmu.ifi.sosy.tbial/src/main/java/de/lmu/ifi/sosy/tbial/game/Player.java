package de.lmu.ifi.sosy.tbial.game;

import de.lmu.ifi.sosy.tbial.BugBlock;
import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.Card.CardType;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
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
  /** The maximum value of mental health points. */
  private int mentalHealthMax;

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

  private LinkedList<BugBlock> bugBlocks = new LinkedList<BugBlock>();

  public Player(String userName) {
    this.userName = userName;
    this.prestige = 0;
    this.fired = false;
    this.mentalHealth = 4;
    this.mentalHealthMax = 4;
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

  public LinkedList<BugBlock> getBugBlocks() {
    return bugBlocks;
  }

  public void blockBug(BugBlock bugBlock) {
    bugBlocks.add(bugBlock);
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
    //mentalHealth = characterCard.getMaxHealthPoints();
    if (roleCard.getRole() == Role.MANAGER) {
      mentalHealth += 1;
    }
    mentalHealthMax = mentalHealth;
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
    if (mentalHealth > mentalHealthMax) {
      mentalHealth = mentalHealthMax;
    }
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
   * For testing only.
   *
   * <p>Removal of all hand cards. Helper function for test!
   *
   * @param cards The player's hand cards to be removed.
   */
  public void removeAllHandCards(Set<StackCard> cards) {
    handCards.removeAll(cards);
  }

  /**
   * Removal of a received card. Removes the card if it is contained in this player's received
   * cards.
   *
   * @param card The card to be removed from the received cards.
   * @return <code>true</code> if the removal was successful, <code>false</code> otherwise
   */
  public boolean removeReceivedCard(StackCard card) {
    return receivedCards.remove(card);
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

  public boolean hasSelectedCard() {
    return selectedHandCard != null;
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

  /**
   * Checks whether player has the fortran maintenance card. If he does, there's a 15% chance this
   * method returns true.
   *
   * @return <code>true</code> if the bug gets blocked and <code>false</code> otherwise
   */
  public boolean hasFortranMaintenanceCard() {
    Stream<StackCard> maintenanceCard =
        receivedCards
            .stream()
            .filter(card -> ((Card) card).getCardType() == CardType.STUMBLING_BLOCK)
            .filter(
                card ->
                    ((StumblingBlockCard) card).getStumblingBlock() == StumblingBlock.MAINTENANCE);
    return maintenanceCard.count() > 0 && Math.random() < 0.15;
  }

  /**
   * Checks whether player has the off-the-job-training card. If he does, there's a 25% chance this
   * method returns true.
   *
   * @return <code>true</code> if the bug gets blocked and <code>false</code> otherwise
   */
  public boolean hasOffTheJobTrainingCard() {
    Stream<StackCard> trainingCard =
        receivedCards
            .stream()
            .filter(card -> ((Card) card).getCardType() == CardType.STUMBLING_BLOCK)
            .filter(
                card -> ((StumblingBlockCard) card).getStumblingBlock() == StumblingBlock.TRAINING);
    return trainingCard.count() > 0 && Math.random() < 0.25;
  }
}
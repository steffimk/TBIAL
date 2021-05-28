package de.lmu.ifi.sosy.tbial.game;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** A game. Contains all information about a game. */
public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(Game.class);

  private String name;

  private int maxPlayers;

  private Map<String, Player> players;

  private String host;

  private boolean isPrivate;

  private String hash;
  private byte[] salt;

  private boolean hasStarted;

  private Stack stack;

  public Game(String name, int maxPlayers, boolean isPrivate, String password, String userName) {
    this.name = requireNonNull(name);
    this.maxPlayers = requireNonNull(maxPlayers);
    if (maxPlayers > 7) {
      this.maxPlayers = 7;
    } else if (maxPlayers < 4) {
      this.maxPlayers = 4;
    }
    this.setHost(requireNonNull(userName));
    this.players = Collections.synchronizedMap(new HashMap<>());

    addNewPlayer(userName);
    // Add three more players for testing
    //    addNewPlayer("A");
    //    addNewPlayer("B");
    //    addNewPlayer("C");
    this.isPrivate = requireNonNull(isPrivate);
    if (isPrivate) {
      requireNonNull(password);
      SecureRandom random = new SecureRandom();
      byte[] saltByteArray = new byte[16];
      random.nextBytes(saltByteArray);
      this.salt = saltByteArray;
      this.hash = getHashedPassword(password, saltByteArray);
    }
    this.hasStarted = false;
  }

  /**
   * Creates a new instance of Player and adds it to the game's players.
   *
   * @param userName
   */
  public void addNewPlayer(String userName) {
    Player newPlayer = new Player(requireNonNull(userName));
    players.put(userName, newPlayer);
  }

  /**
   * Checks whether the game is ready to start and whether this user is allowed to do so.
   *
   * @param username of the user trying to start the game
   * @return Whether this user is allowed to start the game.
   */
  public boolean isAllowedToStartGame(String username) {
    if (username != host) {
      LOGGER.info("Checking if user is allowed to start game: User is not host.");
      return false;
    } else if (players.size() < 4) {
      LOGGER.info("Checking if user is allowed to start game: Game has less than four players.");
      return false;
    } else if (hasStarted) {
      LOGGER.info("Checking if user is allowed to start game: Game has already started.");
      return false;
    }
    return true;
  }

  /** Starts the game. */
  public synchronized void startGame() {
    if (hasStarted) {
      return;
    }
    hasStarted = true;
    distributeRoleCards();
    distributeCharacterCardsAndInitialMentalHealthPoints();
    stack = new Stack();
    distributeInitialHandCards();
  }

  /** Distributing the role cards to the players. */
  private void distributeRoleCards() {
    List<RoleCard> roleCards = RoleCard.getRoleCards(players.size());
    int i = 0;
    for (Player player : players.values()) {
      player.setRoleCard(roleCards.get(i));
      i++;
    }
  }

  /** Distributing the character cards to the players. */
  private void distributeCharacterCardsAndInitialMentalHealthPoints() {
    List<CharacterCard> characterCards = CharacterCard.getCharacterCards(players.size());
    int i = 0;
    for (Player player : players.values()) {
      player.setCharacterCard(characterCards.get(i));
      player.initialMentalHealth();
      i++;
    }
  }

  /**
   * Hands out the initial hand cards to each player. A player receives as many hand cards as he has
   * mental health points.
   */
  private void distributeInitialHandCards() {
    for (Player player : players.values()) {
      Set<StackCard> handCards = new HashSet<>();
      for (int i = 0; i < player.getMentalHealthInt(); i++) {
        handCards.add(stack.drawCard());
      }
      player.addToHandCards(handCards);
    }
  }

  /**
   * Discarding a hand card. Removes the card from the player's hand cards and moves it to the heap.
   * Does not check whether the player is allowed to do so.
   *
   * @param player The player who wants to discard the card.
   * @param card The card the player wants to discard.
   * @return <code>true</code> if the discarding was successful, <code>false</code> otherwise
   */
  public boolean discardHandCard(Player player, StackCard card) {
    if (player.removeHandCard(card)) {
      stack.addToHeap(card);
      System.out.println("Removed card from handcards and added it to heap");
      return true;
    }
    return false;
  }

  /**
   * Removes the card from the player's hand cards and adds it to the receiver's received cards.
   *
   * @param card The card to be played
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   * @return <code>true</code> if the action was successful, <code>false</code> otherwise
   */
  public boolean putCardToPlayer(StackCard card, Player player, Player receiver) {
    // TODO: if (not turn of player) return false;
    if (player.removeHandCard(card)) {
      receiver.receiveCard(card);
      return true; // TODO: maybe receiver needs to respond to this action immediately
    }
    return false;
  }

  /**
   * Checks whether the game already started, is already filled, the player is already inGame and
   * the games privacy
   *
   * @param username
   * @param password
   * @return Whether the play is able/allowed to join the game
   */
  public boolean checkIfYouCanJoin(String username, String password) {
    if (hasStarted()) {
      return false;
    }
    if (getCurrentNumberOfPlayers() >= getMaxPlayers()) {
      return false;
    }
    if (getPlayers().containsKey(username)) {
      return false;
    }
    if (isPrivate() && !getHash().equals(Game.getHashedPassword(password, getSalt()))) {
      return false;
    }
    return true;
  }

  /** Changes the host to the first/next player who isn't set as host */
  public void changeHost() {
    String currentHost = getHost();
    for (Map.Entry<String, Player> entry : getPlayers().entrySet()) {
      if (!entry.getValue().getUserName().equals(currentHost)) {
        setHost(entry.getValue().getUserName());
        break;
      }
    }
  }

  /**
   * Whether the leaving player is the last player in the current game
   *
   * @return
   */
  public boolean checkIfLastPlayer() {
    return getCurrentNumberOfPlayers() == 1;
  }

  public String getName() {
    return name;
  }

  public String getHash() {
    return hash;
  }

  public byte[] getSalt() {
    return salt;
  }

  public int getCurrentNumberOfPlayers() {
    return players.size();
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public Map<String, Player> getPlayers() {
    return players;
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  @Override
  public String toString() {
    return "Game(Name: " + name + ", maxPlayers: " + maxPlayers + ", isPrivate: " + isPrivate + ")";
  }

  /**
   * Returns the hash of password and salt
   *
   * @param password
   * @param salt
   * @return
   */
  public static String getHashedPassword(String password, byte[] salt) {
    String hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] hashByteArray = md.digest(password.getBytes(StandardCharsets.UTF_8));
      hash = new String(hashByteArray, StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hash;
  }

  public boolean hasStarted() {
    return hasStarted;
  }

  public void setHasStarted(boolean hasStarted) {
    this.hasStarted = hasStarted;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = requireNonNull(host);
  }

  public Stack getStack() {
    return stack;
  }

  public void clickedOnHandCard(Player player, StackCard handCard) {
    //   TODO: if(not is turn of player) do nothing
    player.setSelectedHandCard(handCard);
  }

  public void clickedOnHeap(Player player) {
    //   TODO: if(not is turn of player) do nothing
    if (player.getSelectedHandCard() != null) {
      discardHandCard(player, player.getSelectedHandCard());
    }
  }

  /**
   * Called when a player clicks on the "Add Card"-Button of another player. If he has selected a
   * hand card, it will be moved to the block cards of the other player. No rules are checked yet.
   *
   * @param player The player whose turn it should be.
   * @param receiverOfCard The player who should receive the previously selected card.
   */
  public void clickedOnAddCardToPlayer(Player player, Player receiverOfCard) {
    //   TODO: if(not is turn of player) do nothing
    StackCard selectedCard = player.getSelectedHandCard();
    if (selectedCard != null) {
      if (player.removeHandCard(selectedCard)) {
        receiverOfCard.receiveCard(selectedCard);
      }
    }
  }

  /**
   * Called when a player clicks on the "Play Ability"-Button. If he has selected a hand card of the
   * type ability, it will be moved to his uncovered cards. No rules are checked yet.
   *
   * @param player The player whose turn it should be.
   */
  public void clickedOnPlayAbility(Player player) {
    //   TODO: if(not is turn of player) do nothing
    StackCard selectedCard = player.getSelectedHandCard();
    if (selectedCard != null && selectedCard instanceof AbilityCard) {
      if (player.removeHandCard(selectedCard)) {
        player.addPlayedAbilityCard((AbilityCard) selectedCard);
      }
    }
  }
}

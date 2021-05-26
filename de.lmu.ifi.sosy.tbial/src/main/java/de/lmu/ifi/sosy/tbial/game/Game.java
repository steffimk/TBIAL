package de.lmu.ifi.sosy.tbial.game;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
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

  // Checking if the player is allowed to Join
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

  //If the leaving player equals host, host status switches to the next possible player
  public void checkHostChange() {
    String currentHost = getHost();
    Map<String, Player> inGamePlayers = getPlayers();
    for (Map.Entry<String, Player> entry : inGamePlayers.entrySet()) {
      if (!entry.getValue().getUserName().equals(currentHost)) {
        setHost(entry.getValue().getUserName());
        break;
      }
    }
  }

  //Checking, if the leaving player is also the last player
  public boolean checkIfLastPlayer() {
    int currentPlayersInGame = getCurrentNumberOfPlayers();
    if (currentPlayersInGame == 1) {
      return true;
    }
    return false;
  }

  public List<Player> getInGamePlayersList() {
    return new ArrayList<Player>(getPlayers().values());
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
}

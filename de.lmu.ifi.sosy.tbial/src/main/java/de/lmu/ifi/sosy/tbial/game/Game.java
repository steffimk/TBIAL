package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/** A game. */
public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private String name;

  private int maxPlayers;

  private Map<String,Player> players;
  
  private String host;

  private boolean isPrivate;

  private String hash;
  private String salt;

  public Game(String name, int maxPlayers, boolean isPrivate, String password, String userName) {
    this.name = requireNonNull(name);
    this.maxPlayers = requireNonNull(maxPlayers);
    this.host = userName;
    this.players = new HashMap<>();
    addNewPlayer(userName);
    this.isPrivate = requireNonNull(isPrivate);
    if (isPrivate) {
      SecureRandom random = new SecureRandom();
      byte[] saltByteArray = new byte[16];
      random.nextBytes(saltByteArray);
      this.salt = new String(saltByteArray, StandardCharsets.UTF_8);
      this.hash = getHashedPassword(password, saltByteArray);
    }
  }

  /**
   * Creates a new instance of Player and adds it to the game's players.
   *
   * @param userName
   */
  private void addNewPlayer(String userName) {
    Player newPlayer = new Player(userName);
    players.put(userName, newPlayer);
  }

  public String getName() {
    return name;
  }

  public String getHash() {
    return hash;
  }

  public String getSalt() {
    return salt;
  }

  public int getMaxPlayers() {
    return maxPlayers;
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
}

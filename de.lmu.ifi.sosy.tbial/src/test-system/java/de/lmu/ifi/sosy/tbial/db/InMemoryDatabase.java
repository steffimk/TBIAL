package de.lmu.ifi.sosy.tbial.db;

import static java.util.Collections.synchronizedList;
import static java.util.Objects.requireNonNull;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import de.lmu.ifi.sosy.tbial.util.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple in-memory database using a list for managing users.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class InMemoryDatabase implements Database {

  private final List<User> users;
  private final List<Game> games;

  public InMemoryDatabase() {
    users = synchronizedList(new ArrayList<User>());
    games = synchronizedList(new ArrayList<Game>());
  }

  @VisibleForTesting
  protected List<User> getUsers() {
    return users;
  }

  @Override
  public User getUser(String name) {
    requireNonNull(name);
    synchronized (users) {
      for (User user : users) {
        if (name.equals(user.getName())) {
          return user;
        }
      }
      return null;
    }
  }

  @Override
  public boolean userNameTaken(String name) {
    return getUser(name) != null;
  }

  @Override
  public User register(String name, String password) {
    synchronized (users) {
      if (userNameTaken(name)) {
        return null;
      }

      User user = new User(name, password);
      user.setId(users.size());
      users.add(user);

      return user;
    }
  }

  @VisibleForTesting
  protected List<Game> getGames() {
    return games;
  }

  @Override
  public Game newGame(String name, int maxPlayers, boolean isPrivate, String password) {
    synchronized (games) {
      //      if (nameTaken(name)) { TODO SK
      //        return null;
      //      }
      String hash = "", salt = "";
      if (isPrivate) {
        Objects.requireNonNull(password, "password is null");
        SecureRandom random = new SecureRandom();
        byte[] saltByteArray = new byte[16];
        random.nextBytes(saltByteArray);
        hash = SQLDatabase.getHashedPassword(password, saltByteArray);
        salt = new String(saltByteArray, StandardCharsets.UTF_8);
      }

      int id = games.size();
      Game game = new Game(id, name, maxPlayers, isPrivate, hash, salt);
      games.add(game);

      return game;
    }
  }

  @Override
  public Player createPlayer(int userId, int gameId, boolean isHost) {
    // TODO SK: Implement
    return null;
  }

  @Override
  public boolean gameNameTaken(String name) {
    requireNonNull(name);
    synchronized (games) {
      for (Game game : games) {
        if (name.equals(game.getName())) {
          return true;
        }
      }
      return false;
    }
  }
}

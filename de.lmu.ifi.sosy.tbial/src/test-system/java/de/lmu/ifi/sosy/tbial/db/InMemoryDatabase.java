package de.lmu.ifi.sosy.tbial.db;

import static java.util.Collections.synchronizedList;
import static java.util.Objects.requireNonNull;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.game.Game;

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

}

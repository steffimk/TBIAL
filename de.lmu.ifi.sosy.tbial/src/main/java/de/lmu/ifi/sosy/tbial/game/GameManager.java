package de.lmu.ifi.sosy.tbial.game;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Manages the current games. */
public class GameManager {

  private Map<String, Game> currentGames;
  private Map<String, String> userNameXgameName;

  public GameManager() {
    this.currentGames = Collections.synchronizedMap(new HashMap<String, Game>());
    this.userNameXgameName = Collections.synchronizedMap(new HashMap<String, String>());
  }

  public Map<String, Game> getCurrentGames() {
    return currentGames;
  }

  /**
   * Adds the game to the current game and maps the host to the game
   *
   * @param game The new game.
   */
  public synchronized void addGame(Game game) {
    requireNonNull(game);
    if (!gameNameTaken(game.getName())) {
      this.currentGames.put(game.getName(), game);
      this.userNameXgameName.put(game.getHost(), game.getName());
    }
  }

  public synchronized void removeGame(Game game) {
    requireNonNull(game);
    game.getChatMessages().clear();
    this.currentGames.remove(game.getName());
  }

  public boolean gameNameTaken(String name) {
    requireNonNull(name);
    for (String gameName : currentGames.keySet()) {
      if (gameName.equals(name)) {
        return true;
      }
    }
    return false;
  }

  public List<Game> getCurrentGamesAsList() {
    return new ArrayList<Game>(currentGames.values());
  }

  /**
   * Returns the game the user is currently in or <code>null</code> if he is in no game.
   *
   * @param userName The name of the user.
   * @return The game of the user or <code>null</code>
   */
  public Game getGameOfUser(String userName) {
    String gameName = userNameXgameName.get(userName);
    if (gameName == null) return null;
    return currentGames.get(gameName);
  }

  /**
   * Method to join a game and adding the player to the games player list. Maps the user to the
   * game. Checks whether a game with this name exists in the current games.
   *
   * @param username Name of the user wanting to join
   * @param game Name of the game the user wants to join
   * @param password Password of the game if it is password-protected, <code>null</code> otherwise
   */
  public boolean joinGame(String username, Game game, String password) {
    if (game.checkIfYouCanJoin(username, password) && currentGames.containsValue(game)) {
      game.addNewPlayer(username);
      userNameXgameName.put(username, game.getName());
      return true;
    }
    return false;
  }

  /**
   * Removes the user from the map associating user names with game names. Call when a user leaves a
   * game.
   *
   * @param userName The name ot the user to be removed from the map
   */
  public void removeUserFromGame(String userName) {
    userNameXgameName.remove(userName);
  }
}

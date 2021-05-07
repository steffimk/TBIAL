package de.lmu.ifi.sosy.tbial.game;

import java.util.HashMap;

/** Manages the current games. */
public class GameManager {

  private HashMap<String, Game> currentGames;

  public GameManager() {
    this.currentGames = new HashMap<>();
  }

  public HashMap<String, Game> getCurrentGames() {
    return currentGames;
  }

  public void addGame(Game game) {
    if (!gameNameTaken(game.getName())) {
	    this.currentGames.put(game.getName(), game);
	}
  }

  public boolean gameNameTaken(String name) {
    for (String gameName : currentGames.keySet()) {
      if (gameName.equals(name)) {
        return true;
      }
    }
    return false;
  }
}

package de.lmu.ifi.sosy.tbial.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Manages the current games. */
public class GameManager {

  private Map<String, Game> currentGames;

  public GameManager() {
    this.currentGames = Collections.synchronizedMap(new HashMap<String, Game>());
  }

  public Map<String, Game> getCurrentGames() {
    return currentGames;
  }

  public synchronized void addGame(Game game) {
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

  public List<Game> getCurrentGamesAsList() {
    return new ArrayList<Game>(currentGames.values());
  }
}

package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.util.List;

/** Saves information about the stage of the turn and the player whose turn it is. */
public class Turn implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private List<Player> players;
  private int currentPlayerIndex;
  private TurnStage stage;

  public Turn(List<Player> players) {
    this.players = players;
    this.currentPlayerIndex = 0;
    this.stage = TurnStage.DRAWING_CARDS;
  }

  /**
   * Gets the player whose turn it is.
   *
   * @return The player whose turn it is.
   */
  public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);
  }

  /** Switches the turn to the next player who is not fired yet. */
  public void switchToNextPlayer() {
    stage = TurnStage.DRAWING_CARDS;
    currentPlayerIndex++;
    if (currentPlayerIndex == players.size()) {
      currentPlayerIndex = 0;
    }
    while (players.get(currentPlayerIndex).isFired()) {
      switchToNextPlayer();
    }
  }

  /**
   * Call when the player is moving on to the next stage of a turn.
   *
   * @param stage The stage the player moves on to.
   */
  public void setStage(TurnStage stage) {
    this.stage = stage;
  }

  /**
   * Returns the stage of the turn the player is currently in
   *
   * @return the stage
   */
  public TurnStage getStage() {
    return this.stage;
  }

  /** An enum containing the different stages of a turn. */
  public enum TurnStage {
    DRAWING_CARDS,
    PLAYING_CARDS,
    DISCARDING_CARDS
  }

  /**
   * For testing only.
   *
   * @param basePlayer The player to be set as the current player
   */
  public void setTurnPlayerUseForTestingOnly(Player basePlayer) {
    currentPlayerIndex = players.indexOf(basePlayer);
  }
}

package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.AbilityType;
import de.lmu.ifi.sosy.tbial.game.ActionCard.ActionType;
import de.lmu.ifi.sosy.tbial.game.Card.CardType;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;

public class GameStatistics implements Serializable {

  private static final long serialVersionUID = 1L;

  // Action Cards
  private int bugCount;
  private int lameExcuseCount;
  private int solutionCount;
  private int specialActionCount;

  // StumblingBlockCards
  private int maintenanceCount;
  private int trainingCount;

  // Ability Cards
  private int previousJobCount;
  private int garmentCount;
  private int bugDelegationCount;

  // Actions
  private int blockedByDelegationCount;

  public GameStatistics() {
    bugCount = 0;
    lameExcuseCount = 0;
    solutionCount = 0;
    specialActionCount = 0;

    maintenanceCount = 0;
    trainingCount = 0;

    previousJobCount = 0;
    garmentCount = 0;
    bugDelegationCount = 0;

    blockedByDelegationCount = 0;
  }

  /**
   * Call whenever a card is played to save the action in the game statistics
   *
   * @param card The card that is played
   */
  public void playedCard(StackCard card) {
    switch (((Card) card).getCardType()) {
      case STUMBLING_BLOCK:
        playedStumblingBlockCard(((StumblingBlockCard) card).getStumblingBlock());
        break;
      case ABILITY:
        playedAbilityCard(((AbilityCard) card).getAbility().abilityType);
        break;
      case ACTION:
        playedActionCard(((ActionCard) card).getAction().actionType);
        break;
      default:
        break;
    }
  }

  private void playedAbilityCard(AbilityType abilityType) {
    switch (abilityType) {
      case PREVIOUS_JOB:
        previousJobCount++;
        break;
      case GARMENT:
        garmentCount++;
        break;
      case OTHER:
        bugDelegationCount++;
        break;
    }
  }

  private void playedActionCard(ActionType actionType) {
    switch (actionType) {
      case BUG:
        bugCount++;
        break;
      case LAME_EXCUSE:
        lameExcuseCount++;
        break;
      case SOLUTION:
        solutionCount++;
        break;
      case SPECIAL:
        specialActionCount++;
        break;
    }
  }

  private void playedStumblingBlockCard(StumblingBlock stumblingBlock) {
    switch (stumblingBlock) {
      case MAINTENANCE:
        maintenanceCount++;
        break;
      case TRAINING:
        trainingCount++;
        break;
    }
  }

  public void bugDelegationCardBlockedBug() {
    blockedByDelegationCount++;
  }

  public int getBugCount() {
    return bugCount;
  }

  public int getLameExcuseCount() {
    return lameExcuseCount;
  }

  public int getSolutionCount() {
    return solutionCount;
  }

  public int getBlockedByDelegationCount() {
    return blockedByDelegationCount;
  }

  public int getSpecialActionCount() {
    return specialActionCount;
  }

  public int getMaintenanceCount() {
    return maintenanceCount;
  }

  public int getTrainingCount() {
    return trainingCount;
  }

  public int getPreviousJobCount() {
    return previousJobCount;
  }

  public int getGarmentCount() {
    return garmentCount;
  }

  public int getBugDelegationCount() {
    return bugDelegationCount;
  }
}

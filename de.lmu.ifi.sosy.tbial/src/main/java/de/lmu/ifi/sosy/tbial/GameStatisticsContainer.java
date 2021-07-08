package de.lmu.ifi.sosy.tbial;

import java.util.HashSet;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.GameStatistics;
import de.lmu.ifi.sosy.tbial.game.Player;

public class GameStatisticsContainer extends Panel {

  private static final long serialVersionUID = 1L;

  public GameStatisticsContainer(Game currentGame, Player basePlayer) {
    super("gameStatistics");

    MentalHealthChartFactory mhcf =
        new MentalHealthChartFactory(
            new HashSet<Player>(currentGame.getPlayers().values()),
            basePlayer.getNumberOfStoredMentalHealthSnapshots());
    Label startingTimeLabel = new Label("startingTime", currentGame.getStartingTimeAsString());
    Label endingTimeLabel = new Label("endingTime", currentGame.getEndingTimeAsString());
    Label durationLabel = new Label("duration", currentGame.getDurationAsString());
    GameStatistics statistics = currentGame.getStatistics();
    Label bugsLabel = new Label("bugs", statistics.getBugCount());
    Label lameExcusesLabel = new Label("lameExcuses", statistics.getLameExcuseCount());
    Label solutionsLabel = new Label("solutions", statistics.getSolutionCount());
    Label specialActionsLabel = new Label("specialActions", statistics.getSpecialActionCount());
    Label maintenanceLabel = new Label("maintenance", statistics.getMaintenanceCount());
    Label trainingLabel = new Label("training", statistics.getTrainingCount());
    Label previousJobLabel = new Label("previousJob", statistics.getPreviousJobCount());
    Label garmentLabel = new Label("garment", statistics.getGarmentCount());
    Label bugDelegationLabel = new Label("bugDelegation", statistics.getBugDelegationCount());
    Label blockedCardsLabel =
        new Label(
            "blockedCards",
            statistics.getBlockedByDelegationCount()
                + " cards got blocked by a bug delegation card.");

    add(startingTimeLabel);
    add(endingTimeLabel);
    add(durationLabel);
    add(bugsLabel);
    add(lameExcusesLabel);
    add(solutionsLabel);
    add(specialActionsLabel);
    add(maintenanceLabel);
    add(trainingLabel);
    add(previousJobLabel);
    add(garmentLabel);
    add(bugDelegationLabel);
    add(blockedCardsLabel);
    add(mhcf.getNewChartInstance("chart"));
    
    setOutputMarkupId(true);
  }
}

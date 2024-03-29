package de.lmu.ifi.sosy.tbial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

import de.lmu.ifi.sosy.tbial.game.Card;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

/**
 * Returns a droppable that triggers actions when dropping items in, entering and exiting the area.
 */
public class DroppableArea extends Droppable<Void> {

  private static final Logger LOGGER = LogManager.getLogger(DroppableArea.class);

  private static final long serialVersionUID = 1L;
  private DroppableType type;
  private Player playerOfPanel;
  private Game game;
  private Player basePlayer;
  private WebMarkupContainer table;

  /**
   * The constructor of a droppable are.
   *
   * @param id The markup id.
   * @param type The type of this area.
   * @param game The game.
   * @param basePlayer The base player.
   * @param playerOfPanel The player belonging to the panel the droppable area lies in. <code>null
   *     </code> for the heap.
   * @param table The container that holds the game table.
   */
  public DroppableArea(
      String id,
      DroppableType type,
      Game game,
      Player basePlayer,
      Player playerOfPanel,
      WebMarkupContainer table) {
    super(id);
    this.type = type;
    this.game = game;
    this.basePlayer = basePlayer;
    this.playerOfPanel = playerOfPanel;
    this.table = table;
  }

  @Override
  public void onDrop(AjaxRequestTarget target, Component component) {
	  
    Card card = (Card) basePlayer.getSelectedHandCard();
    if (card == null) return;

    if (this.type == DroppableType.HEAP) {
      boolean success = game.clickedOnHeap(basePlayer);
      if (!success) return;
      target.add(table);
      return;
    }

    switch (card.getCardType()) {
      case ABILITY:
        LOGGER.info(
            basePlayer.getUserName()
                + " clicked on play ability button of "
                + playerOfPanel.getUserName());
        game.clickedOnPlayAbility(basePlayer, playerOfPanel);
        break;
      default:
        LOGGER.info(
            basePlayer.getUserName()
                + " dropped card on add card area of "
                + playerOfPanel.getUserName());
        game.clickedOnAddCardToPlayer(basePlayer, playerOfPanel);
        target.add(table);
        break;
    }
    target.add(table);
  }


  public enum DroppableType {
    PLAY_ABILITY,
    ADD_CARD,
    HEAP
  }
}

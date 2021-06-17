package de.lmu.ifi.sosy.tbial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

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

  // private DroppableArea thisArea;

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
    // this.thisArea = this;

    //    this.add(
    //        new AjaxEventBehavior("mouseover") {
    //
    //          private static final long serialVersionUID = 1L;
    //
    //          @Override
    //          protected void onEvent(AjaxRequestTarget target) {
    //            System.out.println("mouseover");
    //            if (thisArea.type != DroppableType.HEAP) {
    //              thisArea.add(
    //                  new AttributeModifier("style", "background: rgba(0, 0, 0, 0) !important;"));
    //              target.add(thisArea);
    //            }
    //          }
    //        });
    //
    //    this.add(
    //        new AjaxEventBehavior("mouseout") {
    //
    //          private static final long serialVersionUID = 1L;
    //
    //          @Override
    //          protected void onEvent(AjaxRequestTarget target) {
    //            System.out.println("mouseout");
    //            if (thisArea.type != DroppableType.HEAP) {
    //              thisArea.add(
    //                  new AttributeModifier(
    //                      "style", "background: rgba(244, 115, 29, 0.5) !important;"));
    //              target.add(thisArea);
    //            }
    //          }
    //        });
  }

  @Override
  public void onDrop(AjaxRequestTarget target, Component component) {
    switch (type) {
      case ADD_CARD:
        LOGGER.info(
            basePlayer.getUserName()
                + " dropped card on add card area of "
                + playerOfPanel.getUserName());
        game.clickedOnAddCardToPlayer(basePlayer, playerOfPanel);
        //        this.add(
        //            new AttributeModifier(
        //                "style", "animation: blinking 0.3s linear; animation-iteration-count:
        // 3;"));
        break;
      case PLAY_ABILITY:
        LOGGER.info(
            basePlayer.getUserName()
                + " clicked on play ability button of "
                + playerOfPanel.getUserName());
        game.clickedOnPlayAbility(basePlayer, playerOfPanel);
        //        this.add(
        //            new AttributeModifier(
        //                "style", "animation: blinking 0.3s linear; animation-iteration-count:
        // 3;"));
        break;
      case HEAP:
        boolean success = game.clickedOnHeap(basePlayer);
        if (!success) return;
        break;
    }
    target.add(table);
  }

//  @Override
//  public void onOver(AjaxRequestTarget target, Component component) {
//    System.out.println("Over");
//    this.add(new AttributeModifier("style", "animation: blinking 0.5s infinite linear;"));
//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//    target.add(this);
//  }

//  @Override
//  public void onExit(AjaxRequestTarget target, Component component) {
//    this.add(new AttributeModifier("style", "animation: none;"));
//    System.out.println("Exit event triggered");
//    target.add(this);
//  }

  //  @Override
  //  public boolean isOverEventEnabled() {
  //    return type != DroppableType.HEAP;
  //  }

  //  @Override
  //  public boolean isExitEventEnabled() {
  //    return type != DroppableType.HEAP;
  //  }

  public enum DroppableType {
    PLAY_ABILITY,
    ADD_CARD,
    HEAP
  }
}

package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.StackAndHeap;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private WebMarkupContainer table;

  public GameTable() {

    table = new WebMarkupContainer("table");
    table.setOutputMarkupId(true);
    // get current game
    Game currentGame = getSession().getCurrentGame();

    // get number of players in current game
    int numberOfPlayers = currentGame.getCurrentNumberOfPlayers();

    // get username of current session player
    String currentPlayerUsername = getSession().getUser().getName();

    add(new Label("gameName", currentGame.getName()));
    
    // get all players of the game
    Map<String, Player> currentPlayers = currentGame.getPlayers();

    // set current session player as base player
    Player basePlayer = currentPlayers.get(currentPlayerUsername);

    // always add current session player here
    WebMarkupContainer player1 = new WebMarkupContainer("player1");

    player1.add(new PlayerAreaPanel("panel1", () -> basePlayer, currentGame, basePlayer, table));
    player1.setOutputMarkupId(true);
    // get the rest of the players
    ArrayList<Player> otherPlayers = new ArrayList<Player>();
    for (Map.Entry<String, Player> entry : currentPlayers.entrySet()) {
      if (!entry.getKey().equals(currentPlayerUsername)) {
        otherPlayers.add(entry.getValue());
      }
    }

    // add player areas of other players to game table
    IModel<List<Player>> playerModel = (IModel<List<Player>>) () -> otherPlayers;

    ListView<Player> playerList =
        new PropertyListView<>("container", playerModel) {
          private static final long serialVersionUID = 1L;

          int i = 2;

          @Override
          protected void populateItem(final ListItem<Player> listItem) {
            final Player player = listItem.getModelObject();
            PlayerAreaPanel panel =
                new PlayerAreaPanel("panel", Model.of(player), currentGame, basePlayer, table);
            // add css classes
            listItem.add(
                new AttributeModifier("class", "player-" + "" + numberOfPlayers + "-" + "" + (i)));
            panel.add(
                new AttributeModifier(
                    "class", "container" + "" + numberOfPlayers + "-" + "" + (i)));
            listItem.add(panel);
            i++;
            if (i > numberOfPlayers) {
              i = 2;
            }
          }
        };

    WebMarkupContainer stackContainer = new WebMarkupContainer("stackContainer");
    stackContainer.add(
        new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            System.out.println("Clicked on stack");

            if (currentGame.getStackAndHeap().getStack().size() == 0) {
              currentGame.getStackAndHeap().refillStack();
            }

            int alreadyDrawnCards = currentGame.getTurn().getDrawnCardsInDrawingStage();
            if (alreadyDrawnCards < Turn.DRAW_LIMIT_IN_DRAWING_STAGE
                && currentGame.getTurn().getCurrentPlayer() == basePlayer) {

              currentGame.drawCardFromStack(basePlayer);
            }

            target.add(table);
          }
        });

    Image stackImage =
        new Image("stackCard", () -> currentGame.getStackAndHeap().getStack().size()) {

          private static final long serialVersionUID = 1L;

          @Override
          protected ResourceReference getImageResourceReference() {

            int currentStackSize = (int) this.getDefaultModelObject();
            float remainingCardsPercentage =
                (float) currentStackSize / (float) StackAndHeap.STACK_SIZE_AT_START;

            if (remainingCardsPercentage > 0 && remainingCardsPercentage < 0.33) {
              return StackImageResourceReferences.smallStackImage;

            } else if (remainingCardsPercentage >= 0.33 && remainingCardsPercentage < 0.66) {
              return StackImageResourceReferences.mediumStackImage;

            } else if (remainingCardsPercentage >= 0.66) {
              return StackImageResourceReferences.bigStackImage;

            } else {
              return StackImageResourceReferences.stackEmptyImage;
            }
          }
        };

    stackImage.setOutputMarkupId(true);
    stackContainer.add(stackImage);

    WebMarkupContainer heapContainer = new WebMarkupContainer("heapContainer");
    Image heapImage =
        new Image("heapCard", () -> currentGame.getStackAndHeap().getUppermostCardOfHeap()) {

          private static final long serialVersionUID = 1L;
          private StackCard previousUppermostHeapCard = null;

          @Override
          public void onConfigure() {
            if (this.getDefaultModelObject() == null) {
              this.setVisible(false);
            } else {
              this.setVisible(true);
            }
            super.onConfigure();
          }

          @Override
          protected void onBeforeRender() {
            StackCard uppermostHeapCard = (StackCard) this.getDefaultModelObject();
            // If no card on heap or card didn't change: no animation
            if (uppermostHeapCard == null || uppermostHeapCard == previousUppermostHeapCard) {
              previousUppermostHeapCard = null;
              this.add(new AttributeModifier("style", "animation-name: none;"));
            }
            // Card changed -> add animation
            else {
              Player player = currentGame.getStackAndHeap().getLastPlayerToDiscardCard();
              this.add(getDiscardingAnimationForPlayer(otherPlayers, player, numberOfPlayers));
            }
            super.onBeforeRender();
            previousUppermostHeapCard = uppermostHeapCard;
          }

          @Override
          protected ResourceReference getImageResourceReference() {
            if (this.getDefaultModelObject() == null) {
              return StackImageResourceReferences.heapEmptyImage;
            }
            return new PackageResourceReference(
                getClass(), ((StackCard) this.getDefaultModelObject()).getResourceFileName());
          }
        };

    heapImage.setOutputMarkupId(true);
    heapContainer.add(heapImage);

    Image heapBackgroundImage =
        new Image("heapBackground", () -> currentGame.getStackAndHeap().getHeap().size()) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onBeforeRender() {
            double remainingCardsPercentage =
                (double) ((int) this.getDefaultModelObject()) / (double) StackAndHeap.HEAP_MAX_SIZE;
            if (remainingCardsPercentage > 0.66) {
              this.add(new AttributeModifier("style", "top: -1vh; left: -0.4vw;"));
            } else if (remainingCardsPercentage > 0.33) {
              this.add(new AttributeModifier("style", "top: -0.6vh; left: -0.3vw;"));
            } else {
              this.add(new AttributeModifier("style", "top: auto; left: auto;"));
            }
            super.onBeforeRender();
          }

          @Override
          protected ResourceReference getImageResourceReference() {
            // heap image changes depending on amount of cards left on the heap
            int currentHeapSize = (int) this.getDefaultModelObject();
            double remainingCardsPercentage =
                (double) currentHeapSize / (double) StackAndHeap.HEAP_MAX_SIZE;

            if (remainingCardsPercentage > 0 && remainingCardsPercentage < 0.33) {
              return StackImageResourceReferences.smallHeapImage;
            } else if (remainingCardsPercentage >= 0.33 && remainingCardsPercentage < 0.66) {
              return StackImageResourceReferences.mediumHeapImage;
            } else if (remainingCardsPercentage >= 0.66) {
              return StackImageResourceReferences.bigHeapImage;
            } else {
              return StackImageResourceReferences.heapEmptyImage;
            }
          }
        };
    heapBackgroundImage.setOutputMarkupId(true);
    heapContainer.add(heapBackgroundImage);

    heapContainer.add(
        new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            boolean success = currentGame.clickedOnHeap(basePlayer);
            if (!success) return;
            target.add(table);
          }

        });

    AjaxLink<Void> playCardsButton =
        new AjaxLink<>("playCardsButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnPlayCardsButton(basePlayer);
          }
        };

    AjaxLink<Void> discardButton =
        new AjaxLink<>("discardButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnDiscardButton(basePlayer);
          }
        };

    AjaxLink<Void> endTurnButton =
        new AjaxLink<>("endTurnButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnEndTurnButton(basePlayer);
          }
        };

    table.add(stackContainer);
    table.add(heapContainer);
    table.add(player1);
    table.add(playerList);
    // Update the table every 20 seconds so that other players can see progress
    // -> Is there a better way for this?
    table.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(20)));

    add(table);
    add(playCardsButton);
    add(discardButton);
    add(endTurnButton);
    add(new ChatPanel("chatPanel", currentGame.getChatMessages()));
 }

  private AttributeModifier getDiscardingAnimationForPlayer(
      List<Player> otherPlayers, Player player, int numberOfPlayers) {
    int playerIndex = 2 + otherPlayers.indexOf(player);
    // If basePlayer
    if (playerIndex == 1) {
      return new AttributeModifier("style", "animation-name: discardAnimation");
    }
    return new AttributeModifier(
        "style", "animation-name: discardAnimation" + numberOfPlayers + "-" + playerIndex);
  }
}

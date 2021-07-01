package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.game.ActionCard;
import de.lmu.ifi.sosy.tbial.game.Card;
import de.lmu.ifi.sosy.tbial.DroppableArea.DroppableType;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.StackAndHeap;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;
import de.lmu.ifi.sosy.tbial.game.Card.CardType;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private WebMarkupContainer table;

  private Game currentGame;

  private Player basePlayer;

  public GameTable() {

    getApplication().getMarkupSettings().setStripWicketTags(true);

    // get current game
    currentGame = getGameManager().getGameOfUser(getSession().getUser().getName());

    // get number of players in current game
    int numberOfPlayers = currentGame.getCurrentNumberOfPlayers();

    // get username of current session player
    String currentPlayerUsername = getSession().getUser().getName();

    add(new Label("gameName", currentGame.getName()));
    
    // get all players of the game
    Map<String, Player> currentPlayers = currentGame.getPlayers();

    // set current session player as base player
    basePlayer = currentPlayers.get(currentPlayerUsername);

    table =
        new WebMarkupContainer("table") {
          /** */
          static final long serialVersionUID = 1L;

          @Override
          public void onBeforeRender() {
            currentGame =
                getGameManager().getGameOfUser(((TBIALSession) getSession()).getUser().getName());

            if (currentGame == null) {
              throw new RestartResponseAtInterceptPageException(Lobby.class);
            }
            // For debugging:
            // System.out.println("GameId: " + System.identityHashCode(currentGame) + "\n");
            super.onBeforeRender();
          }
        };
    table.setOutputMarkupId(true);

    // always add current session player here
    WebMarkupContainer player1 = new WebMarkupContainer("player1");

    player1.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(2)) {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            if (basePlayer.isFired()) {
              player1.add(new AttributeModifier("style", "opacity: 0.4;"));
              stop(target);
            }
          }
        });
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
            if (player.isFired()) {
              listItem.add(new AttributeModifier("style", "opacity: 0.4;"));
            }
            listItem.add(
                new AttributeModifier("class", "player-" + "" + numberOfPlayers + "-" + "" + (i)));
            panel.add(
                new AttributeModifier(
                    "class", "container" + "" + numberOfPlayers + "-" + "" + (i)));
            listItem.add(panel);
            listItem.setOutputMarkupId(true);

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

    DroppableArea heapContainer =
        new DroppableArea(
            "heapContainer", DroppableType.HEAP, currentGame, basePlayer, null, table);
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

            if (remainingCardsPercentage > 0.02 && remainingCardsPercentage < 0.33) {
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

    WebMarkupContainer gameFlowContainer = new WebMarkupContainer("gameflow");
    gameFlowContainer.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(2)) {

          private static final long serialVersionUID = 1L;
          private TurnStage previousTurnStage = null;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            if (previousTurnStage == currentGame.getTurn().getStage()) {
              return;
            }
            previousTurnStage = currentGame.getTurn().getStage();
            target.add(gameFlowContainer);
          }
        });
    gameFlowContainer.setOutputMarkupId(true);

    AjaxLink<Void> drawCardsButton =
        new AjaxLink<>("drawCardsButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(this, TurnStage.DRAWING_CARDS, null);
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            return;
          }
        };

    AjaxLink<Void> playCardsButton =
        new AjaxLink<>("playCardsButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(this, TurnStage.PLAYING_CARDS, TurnStage.DRAWING_CARDS);
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnPlayCardsButton(basePlayer);
            target.add(gameFlowContainer);
          }
        };

    AjaxLink<Void> waitForResponseButton =
        new AjaxLink<>("waitForResponseButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(this, TurnStage.WAITING_FOR_PLAYER_RESPONSE, null);
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            return;
          }
        };

    AjaxLink<Void> discardButton =
        new AjaxLink<>("discardButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(this, TurnStage.DISCARDING_CARDS, TurnStage.PLAYING_CARDS);
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnDiscardButton(basePlayer);
            target.add(gameFlowContainer);
          }
        };

    AjaxLink<Void> endTurnButton =
        new AjaxLink<>("endTurnButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(this, null, TurnStage.DISCARDING_CARDS);
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnEndTurnButton(basePlayer);
            target.add(gameFlowContainer);
          }
        };

    final ModalWindow modal;
    add(modal = new ModalWindow("blockBugModal"));
    modal.setTitle("Bug played against you!");
    modal.setContent(new BugBlockPanel(modal.getContentId(), currentGame, basePlayer));
    modal.setCloseButtonCallback(
        target -> {
          return true;
        });

    table.add(stackContainer);
    table.add(heapContainer);
    table.add(player1);
    table.add(playerList);
    // Update the table every 5 seconds so that other players can see progress
    // -> Is there a better way for this?
    table.add(
        new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {

          private static final long serialVersionUID = 1L;

          @Override
          protected boolean shouldTrigger() {
            // Don't update when it's the baseplayer's turn
            return currentGame.getTurn().getCurrentPlayer() != basePlayer;
          }
        });

    table.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(5)) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            boolean hasLameExcuse = false;
            boolean hasSolution = false;

            for (StackCard card : basePlayer.getHandCards()) {
              if (((Card) card).getCardType() == CardType.ACTION) {
                if (((ActionCard) card).isLameExcuse()) {
                  hasLameExcuse = true;
                }
                if (((ActionCard) card).isSolution()) {
                  hasSolution = true;
                }
              }
            }

            if (!basePlayer.getBugBlocks().isEmpty() && (hasLameExcuse || hasSolution)) {
              if (!modal.isShown()) {
                currentGame
                    .getChatMessages()
                    .add(new ChatMessage(basePlayer.getUserName() + " is making a decision."));
              }
              modal.show(target);
            }
          }
        });

    add(table);

    gameFlowContainer.add(new Label("turnLabel", () -> this.getTextOfTurnLabel()));
    gameFlowContainer.add(drawCardsButton);
    gameFlowContainer.add(playCardsButton);
    gameFlowContainer.add(waitForResponseButton);
    gameFlowContainer.add(discardButton);
    gameFlowContainer.add(endTurnButton);
    add(gameFlowContainer);

    add(new ChatPanel("chatPanel", currentGame.getChatMessages()));

    WebMarkupContainer ceremony = new WebMarkupContainer("ceremony");
    Label ceremonyTitle =
        new Label(
            "ceremonyTitle",
            () -> {
              if (basePlayer.hasWon()) {
                return "Congratulations!";
              } else return "You lost!";
            });
    Label winner =
        new Label(
            "winners",
            () -> {
              if (basePlayer.hasWon()
                  && (basePlayer.getRole() == Role.MANAGER
                      || (basePlayer.getRole() == Role.CONSULTANT
                          && currentGame.allMonkeysFired(otherPlayers)))) {
                return currentGame
                    .getWinners()
                    .replace("has", "have")
                    .replace(basePlayer.getUserName(), "You");
              }
              return currentGame.getWinners().replace(basePlayer.getUserName(), "you");
            });
    winner.setOutputMarkupId(true);
    WebMarkupContainer confetti = new WebMarkupContainer("confetti");
    ceremony.add(winner);
    ceremony.add(ceremonyTitle);
    ceremony.add(confetti);
    Form<?> endGameForm = new Form<>("endGameForm");
    Button endGameButton =
        new Button("endGameButton") {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          public void onSubmit() {
            getTbialApplication().getGameManager().removeUserFromGame(basePlayer.getUserName());
            for (Player player : otherPlayers) {
              getTbialApplication().getGameManager().removeUserFromGame(player.getUserName());
            }
            getTbialApplication().getGameManager().removeGame(currentGame);
            setResponsePage(getApplication().getHomePage());
          }
        };
    endGameForm.add(endGameButton);
    ceremony.add(endGameForm);
    ceremony.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(5)) {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
              if (currentGame.getManager().isFired()) {
                ceremony.add(new AttributeModifier("class", "visible"));
                if (basePlayer.hasWon()) {
                  confetti.add(new AttributeModifier("style", "display: block;"));
                }
                stop(target);
              } else if (currentGame.getConsultant().isFired()
                  && currentGame.allMonkeysFired(currentGame.getEvilCodeMonkeys())) {
                ceremony.add(new AttributeModifier("class", "visible"));
                if (basePlayer.hasWon()) {
                  confetti.add(new AttributeModifier("style", "display: block;"));
                }
                stop(target);
              
            }
            target.add(ceremony);
          }
        });

    add(ceremony);

    MentalHealthChartFactory mhc =
        new MentalHealthChartFactory(new HashSet<Player>(currentGame.getPlayers().values()));
    add(mhc.getNewChartInstance("chart"));
 }

  private AttributeModifier getDiscardingAnimationForPlayer(
      List<Player> otherPlayers, Player player, int numberOfPlayers) {
    int playerIndex = 2 + otherPlayers.indexOf(player);
    // If basePlayer
    if (playerIndex == 1) {
      return new AttributeModifier("style", "animation-name: none;");
    }
    return new AttributeModifier(
        "style", "animation-name: discardAnimation" + numberOfPlayers + "-" + playerIndex);
  }

  /**
   * Add this to the onConfigure method of the game flow links. Enables/disables the link and
   * changes its appearance based on the game state.
   *
   * @param link The link.
   * @param thisStage The stage in which the button shows the current stage.
   * @param transferInStagePossible The stage in which the button should be enabled
   */
  private void onConfigureOfGameFlowButtons(
      AjaxLink<Void> link, TurnStage thisStage, TurnStage transferInStagePossible) {
    link.setEnabled(false);
    TurnStage currentStage = currentGame.getTurn().getStage();
    if (currentGame.isTurnOfPlayer(basePlayer)) {
      if (currentStage == transferInStagePossible) {
        link.setEnabled(true);
        link.add(
            new AttributeModifier(
                "style",
                "background: rgba(244, 115, 29, 0.5) !important; border: 2px solid rgba(244, 115, 29, 0.5) !important;"));
      } else if (currentStage == thisStage) {
        link.add(
            new AttributeModifier(
                "style", "background: #F4731D !important; border: 2px solid #F4731D !important;"));
      } else {
        link.add(
            new AttributeModifier(
                "style", "background: #E8E8E8 !important; border: 2px solid #E8E8E8 !important;"));
      }
    } else {
      link.setEnabled(false);
      if (currentStage == thisStage) {
        link.add(
            new AttributeModifier(
                "style", "background: grey !important; border: 2px solid grey !important;"));
      } else {
        link.add(
            new AttributeModifier(
                "style", "background: #E8E8E8 !important; border: 2px solid #E8E8E8 !important;"));
      }
    }
  }
  
  private String getTextOfTurnLabel() {
	  String currentPlayerName = currentGame.getTurn().getCurrentPlayer().getUserName();
	  if (currentPlayerName == basePlayer.getUserName()) {
		  return "";
	  }
    return "Turn: " + currentPlayerName;
  }
}

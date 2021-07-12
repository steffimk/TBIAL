package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
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

import de.lmu.ifi.sosy.tbial.DroppableArea.DroppableType;

import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.StackAndHeap;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;
import de.lmu.ifi.sosy.tbial.game.Game;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private WebMarkupContainer table;

  private Player basePlayer;

  public GameTable() {
    getApplication().getMarkupSettings().setStripWicketTags(true);

    // get number of players in current game
    int numberOfPlayers = getGame().getCurrentNumberOfPlayers();

    // get username of current session player
    String currentPlayerUsername = getSession().getUser().getName();

    add(new Label("gameName", getGame().getName()));

    // get all players of the game
    Map<String, Player> currentPlayers = getGame().getPlayers();

    // set current session player as base player
    basePlayer = currentPlayers.get(currentPlayerUsername);

    table = new WebMarkupContainer("table");
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
    player1.add(new PlayerAreaPanel("panel1", () -> basePlayer, getSession(), basePlayer, table));
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
                new PlayerAreaPanel(
                    "panel", Model.of(player), (TBIALSession) getSession(), basePlayer, table);
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

            getGame().clickedOnDrawCardsButton(basePlayer);
            target.add(table);
          }
        });

    Image stackImage =
        new Image("stackCard", () -> getGame().getStackAndHeap().getStack().size()) {

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
        new DroppableArea("heapContainer", DroppableType.HEAP, getGame(), basePlayer, null, table);
    Image heapImage =
        new Image("heapCard", () -> getGame().getStackAndHeap().getUppermostCardOfHeap()) {

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
              Player player = getGame().getStackAndHeap().getLastPlayerToDiscardCard();
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
        new Image("heapBackground", () -> getGame().getStackAndHeap().getHeap().size()) {

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
            boolean success = getGame().clickedOnHeap(basePlayer);
            if (!success) return;
            target.add(table);
          }
        });

    heapContainer.add(
        new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {

          private static final long serialVersionUID = 1L;

          protected boolean shouldTrigger() {
            // update when it's the baseplayer's turn
            return getGame().getTurn().getCurrentPlayer() == basePlayer;
          }
        });

    WebMarkupContainer gameFlowContainer = new WebMarkupContainer("gameflow");
    gameFlowContainer.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(2)) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            target.add(gameFlowContainer);
          }
        });
    gameFlowContainer.setOutputMarkupId(true);

    AjaxLink<Void> drawCardsButton =
        new AjaxLink<>("drawCardsButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            if (getGame().isTurnOfPlayer(basePlayer)
                && getGame().getTurn().getStage() == TurnStage.DRAWING_CARDS) {
              this.setEnabled(true);
              this.add(getAttributeModifierForLink("#F4731D"));
            } else {
              onConfigureOfGameFlowButtons(this, TurnStage.DRAWING_CARDS, null);
            }
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            getGame().clickedOnDrawCardsButton(basePlayer);
            target.add(table);
            target.add(gameFlowContainer);
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
            return;
          }
        };

    AjaxLink<Void> waitForResponseButton =
        new AjaxLink<>("waitForResponseButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            onConfigureOfGameFlowButtons(
                this, TurnStage.WAITING_FOR_PLAYER_RESPONSE, TurnStage.CHOOSING_CARD_TO_BLOCK_WITH);
            if (getGame().getTurn().getStage() == TurnStage.CHOOSING_CARD_TO_BLOCK_WITH) {
              onConfigureOfGameFlowButtons(this, TurnStage.CHOOSING_CARD_TO_BLOCK_WITH, null);
            }

            this.setEnabled(false);
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
            getGame().clickedOnDiscardButton(basePlayer);
            target.add(gameFlowContainer);
          }
        };

    AjaxLink<Void> endTurnButton =
        new AjaxLink<>("endTurnButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onConfigure() {
            TurnStage currentStage = getGame().getTurn().getStage();
            if (getGame().isTurnOfPlayer(basePlayer)
                && (currentStage == TurnStage.PLAYING_CARDS
                    || currentStage == TurnStage.DISCARDING_CARDS)
                && basePlayer.canEndTurn()) {
              this.setEnabled(true);
              this.add(getAttributeModifierForLink("rgba(244, 115, 29, 0.5)"));
            } else {
              onConfigureOfGameFlowButtons(this, null, null);
            }
            super.onConfigure();
          }

          @Override
          public void onClick(AjaxRequestTarget target) {
            getGame().clickedOnEndTurnButton(basePlayer);
            target.add(gameFlowContainer);
          }
        };

    final ModalWindow modal;
    add(modal = new ModalWindow("blockBugModal"));
    modal.setTitle("Bug played against you!");
    modal.showUnloadConfirmation(false);
    modal.setContent(new BugBlockPanel(modal.getContentId(), getSession(), basePlayer));
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
            return getGame().getTurn().getCurrentPlayer() != basePlayer;
          }
        });

    table.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            Game currentGame = getGame();

            // Update exclusively player's table who played the bug
            if (currentGame.getHasPlayedBugBeenDefended()
                && currentGame.getTurn().getCurrentPlayer() == basePlayer) {
              currentGame.setHasPlayedBugBeenDefended(false);
              setResponsePage(getPage());
            }

            if (basePlayer == currentGame.getTurn().getAttackedPlayer()) {
              boolean canDefendBug = basePlayer.canDefendBug();

              if (canDefendBug
                  && currentGame.getTurn().getStage() != TurnStage.CHOOSING_CARD_TO_BLOCK_WITH) {
                if (!modal.isShown()) {
                  currentGame
                      .getChatMessages()
                      .add(
                          new ChatMessage(
                              basePlayer.getUserName() + " is making a decision.", false, "all"));
                }
                modal.show(target);
              } else if (!canDefendBug
                  && basePlayer
                      .getReceivedCards()
                      .contains(currentGame.getTurn().getLastPlayedBugCard())) {
                currentGame.putCardOnHeap(basePlayer, currentGame.getTurn().getLastPlayedBugCard());
                basePlayer.getReceivedCards().remove(currentGame.getTurn().getLastPlayedBugCard());
                currentGame.getTurn().setStage(TurnStage.PLAYING_CARDS);
                currentGame.getTurn().setAttackedPlayer(null);
                currentGame.getTurn().setLastPlayedBugCard(null);
                currentGame.getTurn().setLastPlayedBugCardBy(null);

                currentGame
                    .getChatMessages()
                    .addFirst(
                        new ChatMessage(
                            basePlayer.getUserName() + " is making a decision.", false, "all"));
              }
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

    add(new ChatPanel("chatPanel", getSession(), true));

    WebMarkupContainer ceremony = new WebMarkupContainer("ceremony");
    Label ceremonyTitle =
        new Label(
            "ceremonyTitle",
            () -> {
              if (basePlayer.hasWon()) {
                return "Congratulations!";
              } else return "You lost!";
            });
    Label groupWon =
        new Label(
            "groupWon",
            () -> {
              return getGame().getGroupWon();
            });

    Label winner =
        new Label(
            "winners",
            () -> {
              return getGame().getWinners(basePlayer);
            });

    winner.setOutputMarkupId(true);
    groupWon.setOutputMarkupId(true);
    WebMarkupContainer confetti = new WebMarkupContainer("confetti");
    ceremony.add(winner);
    ceremony.add(groupWon);
    ceremony.add(ceremonyTitle);
    ceremony.add(confetti);
    Form<?> endGameForm = new Form<>("endGameForm");
    Button endGameButton =
        new Button("endGameButton") {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          public void onSubmit() {
            Game game = getGame();
            getGameManager().removeUserFromGame(basePlayer.getUserName());
            for (Player player : otherPlayers) {
              getGameManager().removeUserFromGame(player.getUserName());
            }
            getGameManager().removeGame(game);
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
            if (getGame().getManager().isFired()) {
              ceremony.add(new AttributeModifier("class", "visible"));
              if (basePlayer.hasWon()) {
                confetti.add(new AttributeModifier("style", "display: block;"));
              }
              ceremony.replace(new GameStatisticsContainer(getGame(), basePlayer));
              stop(target);
            } else if (getGame().getConsultant().isFired()
                && getGame().allMonkeysFired(getGame().getEvilCodeMonkeys())) {
              ceremony.add(new AttributeModifier("class", "visible"));
              if (basePlayer.hasWon()) {
                confetti.add(new AttributeModifier("style", "display: block;"));
              }
              ceremony.replace(new GameStatisticsContainer(getGame(), basePlayer));
              stop(target);
            }
            target.add(ceremony);
          }
        });

    ceremony.add(new GameStatisticsContainer(getGame(), basePlayer));
    add(ceremony);
 }

  private AttributeModifier getDiscardingAnimationForPlayer(
      List<Player> otherPlayers, Player player, int numberOfPlayers) {
    int playerIndex = 2 + otherPlayers.indexOf(player);
    // If basePlayer
    if (playerIndex == 1) {
      if (getGame().getStackAndHeap().wasNormalDiscard()) {
        return new AttributeModifier("style", "animation-name: none;");
      } else {
        return new AttributeModifier("style", "animation-name: discardAnimation");
      }
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
    TurnStage currentStage = getGame().getTurn().getStage();
    if (getGame().isTurnOfPlayer(basePlayer)) {
      if (currentStage == transferInStagePossible) {
        link.setEnabled(true);
        link.add(getAttributeModifierForLink("rgba(244, 115, 29, 0.5)"));
      } else if (currentStage == thisStage) {
        link.add(getAttributeModifierForLink("#F4731D"));
      } else {
        link.add(getAttributeModifierForLink("#E8E8E8"));
      }
    } else {
      if (currentStage == thisStage) {
        link.add(getAttributeModifierForLink("grey"));
      } else {
        link.add(getAttributeModifierForLink("#E8E8E8"));
      }
    }
  }
  
  private String getTextOfTurnLabel() {
    String currentPlayerName = getGame().getTurn().getCurrentPlayer().getUserName();
	  if (currentPlayerName == basePlayer.getUserName()) {
		  return "";
	  }
    return "Turn: " + currentPlayerName;
  }

  private AttributeModifier getAttributeModifierForLink(String color) {
    return new AttributeModifier(
        "style",
        "background: " + color + " !important; border: 2px solid " + color + " !important;");
  }
}

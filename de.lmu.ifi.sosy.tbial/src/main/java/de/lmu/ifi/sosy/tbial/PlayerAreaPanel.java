package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.core.resource.StyleSheetPackageHeaderItem;
import com.googlecode.wicket.jquery.ui.interaction.draggable.Draggable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.DroppableArea.DroppableType;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(PlayerAreaPanel.class);

  private TBIALSession session;

  public PlayerAreaPanel(
      String id,
      IModel<Player> player,
      TBIALSession session,
      Player basePlayer,
      WebMarkupContainer table) {
    super(id, new CompoundPropertyModel<Player>(player));

    this.session = session;

    add(new Label("userName"));
    Label role = new Label("roleName");
    role.setVisible(false);
    if (player.getObject().getRoleName() == "Manager"
        || player.getObject().isFired()
        || player.getObject().equals(basePlayer)) {
      role.setVisible(true);
    }
    role.setOutputMarkupPlaceholderTag(true);
    add(role);
    Label mentalHealth = new Label("mentalHealth");

    add(mentalHealth);
    add(
        new Label(
            "prestige",
            () -> "Prestige: " + getGame().calculatePrestige(basePlayer, player.getObject())));

    // update mental health; if mental health == 0 (-> fire player) -> show role of player on game
    // table
    mentalHealth.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            Game game = getGame();
            if (player.getObject().getMentalHealthInt() <= 0) {
              game.firePlayer(player.getObject(), player.getObject().getHandCards());
              role.setVisible(true);
              target.add(role);
              target.add(getParent().add(new AttributeModifier("style", "opacity: 0.4;")));
              if (game.getTurn().getCurrentPlayer() == player.getObject()) {
                game.getTurn().switchToNextPlayer();
              }
              stop(target);
            }

            target.add(mentalHealth);
          }
        });

    // --------------------------- The played cards ---------------------------
    AjaxLink<Void> playAbilityButton =
        new AjaxLink<Void>("playAbilityButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            LOGGER.info(
                basePlayer.getUserName()
                    + " clicked on play ability button of "
                    + player.getObject().getUserName());
            getGame().clickedOnPlayAbility(basePlayer, player.getObject());
            target.add(table);
          }
        };
    Droppable<Void> playAbilityDropBox =
        new DroppableArea(
            "playAbilityDropBox",
            DroppableType.PLAY_ABILITY,
            getGame(),
            basePlayer,
            player.getObject(),
            table);
    playAbilityDropBox.add(playAbilityButton);

    IModel<List<StackCard>> playedAbilityCardsModel =
        (IModel<List<StackCard>>)
            () -> new ArrayList<StackCard>(player.getObject().getPlayedAbilityCards());
    ListView<StackCard> playedAbilityCards =
        new PropertyListView<>("playedCards", playedAbilityCardsModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<StackCard> item) {
            final StackCard abilityCard = item.getModelObject();
            item.add(
                new Image(
                    "playedCard",
                    new PackageResourceReference(getClass(), abilityCard.getResourceFileName())));
          }
        };
    playAbilityDropBox.add(playedAbilityCards);

    add(playAbilityDropBox);

    // --------------------------- The block cards ---------------------------
    AjaxLink<Void> addCardButton =
        new AjaxLink<Void>("addCardButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            LOGGER.info(
                basePlayer.getUserName()
                    + " clicked on add card button of "
                    + player.getObject().getUserName());
            getGame().clickedOnAddCardToPlayer(basePlayer, player.getObject());

            target.add(table);
          }
        };
    Droppable<Void> addCardDropBox =
        new DroppableArea(
            "addCardDropBox",
            DroppableType.ADD_CARD,
            getGame(),
            basePlayer,
            player.getObject(),
            table);
    addCardDropBox.add(addCardButton);

    IModel<List<StackCard>> blockCardModel =
        (IModel<List<StackCard>>)
            () -> new ArrayList<StackCard>(player.getObject().getReceivedCards());
    ListView<StackCard> blockCards =
        new PropertyListView<>("blockCards", blockCardModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<StackCard> item) {
            final StackCard blockCard = item.getModelObject();
            item.add(
                new Image(
                    "blockCard",
                    new PackageResourceReference(getClass(), blockCard.getResourceFileName())));
          }
        };
    addCardDropBox.add(blockCards);

    add(addCardDropBox);
    addCardDropBox.add(
        new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {

          private static final long serialVersionUID = 1L;

          @Override
          protected boolean shouldTrigger() {
            // update when it's the baseplayer's turn
            return getGame().getTurn().getCurrentPlayer() == basePlayer;
          }
        });

    // --------------------------- The hand cards ---------------------------

    WebMarkupContainer handCardContainer = new WebMarkupContainer("handCardContainer");
    handCardContainer.setOutputMarkupId(true);
    /** adding hand cards to the player area panel */
    IModel<List<StackCard>> handCardModel =
        (IModel<List<StackCard>>) () -> new ArrayList<StackCard>(player.getObject().getHandCards());
    ListView<StackCard> handCardList =
        new PropertyListView<>("handcards", handCardModel) {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<StackCard> listItem) {
            final StackCard handCard = listItem.getModelObject();
            if (player.getObject().equals(basePlayer)) {
              listItem.add(
                  new AjaxEventBehavior("click") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                      LOGGER.info(player.getObject().getUserName() + " clicked on a hand card");
                      getGame().clickedOnHandCard(player.getObject(), handCard);
                      target.add(handCardContainer);
                    }
                  });
              Image card =
                  new Image(
                      "handCard",
                      new PackageResourceReference(getClass(), handCard.getResourceFileName()));
              card.setOutputMarkupId(true);
              // If it is the turn of the player: Cards are draggable
              if (getGame().getTurn().getCurrentPlayer() == player.getObject()) {
                if (player.getObject().getSelectedHandCard() == handCard) {
                  card.add(new AttributeModifier("class", "handcard selected"));
                }
                Draggable<StackCard> draggable =
                    getNewDraggableInstance(handCard, player.getObject());
                draggable.add(card);
                listItem.add(draggable);
              } else {
                WebMarkupContainer notDraggable = new WebMarkupContainer("draggable");
                notDraggable.add(card);
                listItem.add(notDraggable);
              }
            } else {
              WebMarkupContainer notDraggable = new WebMarkupContainer("draggable");
              notDraggable.add(new Image("handCard", StackImageResourceReferences.cardBackImage));
              listItem.add(notDraggable);
            }
          }
        };

    handCardContainer.add(handCardList);
    add(handCardContainer);
  }

  @Override
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    response.render(new StyleSheetPackageHeaderItem(GameTable.class));
  }

  /**
   * Returns a new instance of a draggable
   *
   * @param card The card which the draggable contains
   * @param game The current game
   * @param player The player whose card it is
   * @return
   */
  private Draggable<StackCard> getNewDraggableInstance(StackCard card, Player player) {
    return new Draggable<StackCard>("draggable", () -> card) {
      private static final long serialVersionUID = 1L;

      @Override
      public void onDragStart(AjaxRequestTarget target, int top, int left) {
        getGame().clickedOnHandCard(player, card);
        getApplication().getMarkupSettings().setStripWicketTags(true);
        Component handCard =
            this.get("handCard")
                .add(new AttributeModifier("style", "transform: scale(1) !important;"));
        target.add(handCard);
      }
    };
  }

  /**
   * Gets the current game of the user in the session or redirects to the Lobby.
   *
   * @return The current game or redirects to the Lobby.
   */
  private Game getGame() {
    if (session == null || session.getUser() == null || session.getUser().getName() == null) {
      throw new RestartResponseAtInterceptPageException(Lobby.class);
    }
    return session.getGame();
  }
}

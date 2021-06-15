package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.jquery.ui.interaction.draggable.Draggable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(PlayerAreaPanel.class);

  public PlayerAreaPanel(
      String id, IModel<Player> player, Game game, Player basePlayer, WebMarkupContainer table) {
    super(id, new CompoundPropertyModel<Player>(player));

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
    add(new Label("prestige"));

    // update mental health; if mental health == 0 (-> fire player) -> show role of player on game table
    mentalHealth.add(
        new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onTimer(AjaxRequestTarget target) {
            if (player.getObject().getMentalHealthInt() == 0) {
              player.getObject().fire(true);
              role.setVisible(true);
              target.add(role);
              stop(target);
            }
            // TODO maybe this update should be triggered somewhere else in the future
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
            game.clickedOnPlayAbility(basePlayer, player.getObject());
            target.add(table);
          }
        };
    Droppable<Void> playAbilityDropBox = this.newDroppable("playAbilityDropBox");
    playAbilityDropBox.add(playAbilityButton);
    add(playAbilityDropBox);

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
    add(playedAbilityCards);

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
            game.clickedOnAddCardToPlayer(basePlayer, player.getObject());
            target.add(table);
          }
        };
    Droppable<Void> addCardDropBox = this.newDroppable("addCardDropBox");
    addCardDropBox.add(addCardButton);
    add(addCardDropBox);

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
    add(blockCards);

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
                      game.clickedOnHandCard(player.getObject(), handCard);
                      target.add(handCardContainer);
                    }
                  });
              Image card =
                  new Image(
                      "handCard",
                      new PackageResourceReference(getClass(), handCard.getResourceFileName()));
              if (player.getObject().getSelectedHandCard() == handCard) {
                card.add(new AttributeModifier("class", "handcard selected"));
                Draggable<Void> draggable = newDraggable();
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
    // TODO: Is this necessary?
    // response.render(new StyleSheetPackageHeaderItem(GameTable.class));
  }

  /**
   * Returns a new draggable component for a handcard
   *
   * @return The new draggable component
   */
  private Draggable<Void> newDraggable() {
    return new Draggable<Void>("draggable") {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean isStopEventEnabled() {
        return true;
      }

      @Override
      public void onDragStart(AjaxRequestTarget target, int top, int left) {
        LOGGER.info(String.format("Drag started - position: {%s, %s}", top, left));
      }

      @Override
      public void onDragStop(AjaxRequestTarget target, int top, int left) {
        double offsetTop = RequestCycleUtils.getQueryParameterValue("offsetTop").toDouble(-1);
        double offsetLeft = RequestCycleUtils.getQueryParameterValue("offsetLeft").toDouble(-1);

        this.info(
            String.format(
                "Drag stoped - position: {%d, %d}, offset: {%.1f, %.1f}",
                top, left, offsetTop, offsetLeft));
      }
    };
  }

  /**
   * Gets a new Droppable.<br>
   * By default 'drag-enter' and 'drag-leave' events are disabled to minimize client/server
   * round-trips.
   */
  private Droppable<Void> newDroppable(String id) {
    return new Droppable<>(id) {

      private static final long serialVersionUID = 1L;

      @Override
      public void onDrop(AjaxRequestTarget target, Component component) {
        // TODO Auto-generated method stub

      }

      @Override
      public MarkupContainer setDefaultModel(IModel<?> model) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}

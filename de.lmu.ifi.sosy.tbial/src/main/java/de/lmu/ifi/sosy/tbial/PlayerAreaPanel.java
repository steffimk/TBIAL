package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
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

import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(PlayerAreaPanel.class);

  public static PackageResourceReference cardBackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/backSide.png");

  public static PackageResourceReference bigStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackBig.png");

  public static PackageResourceReference mediumStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackMedium.png");

  public static PackageResourceReference smallStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackSmall.png");

  public static PackageResourceReference stackEmptyImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackEmpty.png");

  public static PackageResourceReference heapEmptyImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/HeapEmpty.png");

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
            LOGGER.info(basePlayer.getUserName() + " clicked on play ability button");
            game.clickedOnPlayAbility(basePlayer);
            target.add(table);
          }

          @Override
          public boolean isVisible() {
            return player.getObject().equals(basePlayer);
          }
        };
    add(playAbilityButton);

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

    add(addCardButton);

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
              listItem.add(card);
              if (player.getObject().getSelectedHandCard() == handCard) {
                card.add(new AttributeModifier("class", "handcard selected"));
              }
            } else {
              listItem.add(new Image("handCard", cardBackImage));
            }
          }
        };

    handCardContainer.add(handCardList);
    add(handCardContainer);
  }

}

package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.AbilityCard;
import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public PlayerAreaPanel(String id, IModel<Player> player, Game game) {
    super(id, new CompoundPropertyModel<Player>(player));
    add(new Label("userName"));
    Label role = new Label("roleName");
    role.setVisible(false);
    if (player.getObject().getRoleName() == "Manager"
        || player.getObject().isFired()
        || player.getObject().isBasePlayer()) {
      role.setVisible(true);
    }
    add(role);
    add(new Label("mentalHealth"));
    add(new Label("prestige"));
    add(new Label("bug"));

    IModel<List<StackCard>> blockCardModel =
        (IModel<List<StackCard>>)
            () -> new ArrayList<StackCard>(player.getObject().getReceivedCards());
    switch (blockCardModel.getObject().size()) {
      case 2:
        System.out.println("Hi");
        blockCardModel.getObject().add(new AbilityCard(Ability.GOOGLE));
      case 1:
        blockCardModel.getObject().add(new AbilityCard(Ability.GOOGLE));
        break;
    }
    ListView<StackCard> blockCards =
        new PropertyListView<>("blockCards", blockCardModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<StackCard> item) {
            final StackCard blockCard = item.getModelObject();
            if (blockCard instanceof AbilityCard
                && ((AbilityCard) blockCard).getAbility() == Ability.GOOGLE) {
              item.add(new Image("blockCard", "imgs/cards/backSide.png"));
            } else {
              item.add(new Image("blockCard", blockCard.getResourceFileName()));
            }
          }
        };
    add(blockCards);
    //
    //    WebMarkupContainer blockCardPlaceholder1 = new WebMarkupContainer("placeholder1");
    //    WebMarkupContainer blockCardPlaceholder2 = new WebMarkupContainer("placeholder2");
    //    blockCardPlaceholder1.add(
    //        new AjaxEventBehavior("click") {
    //          private static final long serialVersionUID = 1L;
    //
    //          @Override
    //          protected void onEvent(AjaxRequestTarget target) {
    //            System.out.println("Clicked on Placeholder");
    //            // game.clickedOnBlockedCardPlaceholder(player.getObject());
    //          }
    //        });
    //    blockCardPlaceholder2.add(
    //        new AjaxEventBehavior("click") {
    //          private static final long serialVersionUID = 1L;
    //
    //          @Override
    //          protected void onEvent(AjaxRequestTarget target) {
    //            System.out.println("Clicked on Placeholder");
    //            // game.clickedOnBlockedCardPlaceholder(player.getObject());
    //          }
    //        });
    //    blockCardPlaceholder1.setOutputMarkupId(true);
    //    blockCardPlaceholder2.setOutputMarkupId(true);
    //    if (player.getObject().getReceivedCards().size() > 0) {
    //      blockCardPlaceholder2.setVisible(false);
    //      if (player.getObject().getReceivedCards().size() > 1) {
    //        blockCardPlaceholder2.setVisible(false);
    //      }
    //    }
    //    add(blockCardPlaceholder1);
    //    add(blockCardPlaceholder2);

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

            WebMarkupContainer imageContainer = new WebMarkupContainer("cardContainer");

            if (player.getObject().isBasePlayer()) {
              imageContainer.add(
                  new AjaxEventBehavior("click") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                      System.out.println("Clicked on " + handCard.toString());
                      game.clickedOnHandCard(player.getObject(), handCard);
                    }
                  });
              imageContainer.add(new Image("handCard", handCard.getResourceFileName()));
            } else {
              imageContainer.add(new Image("handCard", "imgs/cards/backSide.png"));
            }

            listItem.add(imageContainer);
          }
        };

    add(handCardList);
  }

}

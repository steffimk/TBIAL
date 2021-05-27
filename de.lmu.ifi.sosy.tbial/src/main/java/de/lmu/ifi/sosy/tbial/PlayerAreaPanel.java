package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
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

  public PlayerAreaPanel(String id, IModel<Player> player, Game game, Player basePlayer) {
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

    // Adding block cards to the panel
    WebMarkupContainer addCardButton = new WebMarkupContainer("addCardButton");
    addCardButton.add(
        new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            System.out.println("Clicked on Add Card of " + player.getObject().getUserName());
            game.clickedOnAddCardToPlayer(basePlayer, player.getObject());
          }
        });
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
            item.add(new Image("blockCard", blockCard.getResourceFileName()));
          }
        };
    add(blockCards);

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

            if (player.getObject().isBasePlayer()) {
              listItem.add(
                  new AjaxEventBehavior("click") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                      System.out.println("Clicked on " + handCard.toString());
                      game.clickedOnHandCard(player.getObject(), handCard);
                    }
                  });
              listItem.add(new Image("handCard", handCard.getResourceFileName()));
              if (player.getObject().getSelectedHandCard() == handCard) {
                System.out.println("Add border to handCard");
                listItem.add(new AttributeModifier("class", "handcard selected"));
              }
            } else {
              listItem.add(new Image("handCard", "imgs/cards/backSide.png"));
            }
          }
        };

    add(handCardList);
  }

}

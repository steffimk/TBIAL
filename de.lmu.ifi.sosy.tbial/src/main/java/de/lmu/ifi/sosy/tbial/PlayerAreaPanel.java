package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import de.lmu.ifi.sosy.tbial.game.StackCard;

import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public PlayerAreaPanel(String id, IModel<Player> player) {
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
              listItem.add(new Image("handCard", handCard.getResourceFileName()));
            } else {
              listItem.add(new Image("handCard", "imgs/cards/backSide.png"));
            }
          }
        };

    add(handCardList);
  }
}

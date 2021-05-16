package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

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
    add(new Label("bug"));
  }
}

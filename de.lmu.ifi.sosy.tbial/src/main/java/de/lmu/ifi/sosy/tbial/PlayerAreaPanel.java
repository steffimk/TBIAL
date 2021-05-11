package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.game.Player;

public class PlayerAreaPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public PlayerAreaPanel(String id, Player player) {
    super(id);
    add(new Label("userName", new PropertyModel<Player>(player, "userName")));
    add(new Label("character", new PropertyModel<Player>(player, "character")));
    add(new Label("mentalHealth", new PropertyModel<Player>(player, "mentalHealth")));
    add(new Label("prestige", new PropertyModel<Player>(player, "prestige")));
    add(new Label("bug", new PropertyModel<Player>(player, "bug")));
    //		add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    //		setOutputMarkupId(true);
  }
}

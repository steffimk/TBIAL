package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public GameTable() {
    int players = 4;
    WebMarkupContainer container = new WebMarkupContainer("myContainer");
    add(container);
    if (players != 4) {
      container.setVisible(false);
    }
  }
}

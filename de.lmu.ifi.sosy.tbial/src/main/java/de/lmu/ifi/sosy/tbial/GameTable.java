package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public GameTable() {
    int players = 7;
    WebMarkupContainer container4 = new WebMarkupContainer("myContainer4");
    WebMarkupContainer container5 = new WebMarkupContainer("myContainer5");
    WebMarkupContainer container6 = new WebMarkupContainer("myContainer6");
    WebMarkupContainer container7 = new WebMarkupContainer("myContainer7");
    add(container4);
    add(container5);
    add(container6);
    add(container7);
    container4.setVisible(false);
    container5.setVisible(false);
    container6.setVisible(false);
    container7.setVisible(false);
    if (players == 4) {
        container4.setVisible(true);
      }
    if (players == 5) {
      container5.setVisible(true);
    }
    if (players == 6) {
        container6.setVisible(true);
    }
    if (players == 7) {
        container7.setVisible(true);
    }
  }
}

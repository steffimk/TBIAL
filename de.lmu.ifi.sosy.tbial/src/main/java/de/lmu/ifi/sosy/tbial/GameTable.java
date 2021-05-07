package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.Component;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  
  //private Component playerAreaPanel;
  //private Fragment fragment;

  public GameTable() {
    int players = 4;
    WebMarkupContainer container = new WebMarkupContainer("myContainer");
    add(container);
    
    //add(playerAreaPanel = new PlayerAreaPanel("playerAreaPanel"));
    //add(fragment = new Fragment("myContainer", "playerArea", this));
    
    if (players != 4) {
      container.setVisible(false);
    }
  }
}

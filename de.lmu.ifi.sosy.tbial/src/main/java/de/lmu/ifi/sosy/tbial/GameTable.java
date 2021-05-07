package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  
  //private Component playerAreaPanel;
  //private Fragment fragment;
  PlayerAreaPanel panel = new PlayerAreaPanel("panel1");
  PlayerAreaPanel panel2 = new PlayerAreaPanel("panel4-2");
  PlayerAreaPanel panel3 = new PlayerAreaPanel("panel4-3");
  PlayerAreaPanel panel4 = new PlayerAreaPanel("panel4-4");
  PlayerAreaPanel panel5 = new PlayerAreaPanel("panel5-2");
  PlayerAreaPanel panel6 = new PlayerAreaPanel("panel5-3");
  PlayerAreaPanel panel7 = new PlayerAreaPanel("panel5-4");
  PlayerAreaPanel panel8 = new PlayerAreaPanel("panel5-5");
  PlayerAreaPanel panel9 = new PlayerAreaPanel("panel6-2");
  PlayerAreaPanel panel10 = new PlayerAreaPanel("panel6-3");
  PlayerAreaPanel panel11 = new PlayerAreaPanel("panel6-4");
  PlayerAreaPanel panel12 = new PlayerAreaPanel("panel6-5");
  PlayerAreaPanel panel13 = new PlayerAreaPanel("panel6-6");
  PlayerAreaPanel panel14 = new PlayerAreaPanel("panel7-2");
  PlayerAreaPanel panel15 = new PlayerAreaPanel("panel7-3");
  PlayerAreaPanel panel16 = new PlayerAreaPanel("panel7-4");
  PlayerAreaPanel panel17 = new PlayerAreaPanel("panel7-5");
  PlayerAreaPanel panel18 = new PlayerAreaPanel("panel7-6");
  PlayerAreaPanel panel19 = new PlayerAreaPanel("panel7-7");

  public GameTable() {
    
    int players = 4;
    WebMarkupContainer player1 = new WebMarkupContainer("player1");
    WebMarkupContainer container4 = new WebMarkupContainer("myContainer4");
    WebMarkupContainer container5 = new WebMarkupContainer("myContainer5");
    WebMarkupContainer container6 = new WebMarkupContainer("myContainer6");
    WebMarkupContainer container7 = new WebMarkupContainer("myContainer7");
    add(player1);
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
    player1.add(panel);
    container4.add(panel2);
    container4.add(panel3);
    container4.add(panel4);
    container5.add(panel5);
    container5.add(panel6);
    container5.add(panel7);
    container5.add(panel8);
    container6.add(panel9);
    container6.add(panel10);
    container6.add(panel11);
    container6.add(panel12);
    container6.add(panel13);
    container7.add(panel14);
    container7.add(panel15);
    container7.add(panel16);
    container7.add(panel17);
    container7.add(panel18);
    container7.add(panel19);
  }
}

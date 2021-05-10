package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;
import de.lmu.ifi.sosy.tbial.game.Player;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

 
  public GameTable() {
	  	  
	// to do get max number of players from game -> wait for merge
    int players = 4;
    WebMarkupContainer player1 = new WebMarkupContainer("player1");
    WebMarkupContainer container4 = new WebMarkupContainer("myContainer4");
    WebMarkupContainer container5 = new WebMarkupContainer("myContainer5");
    WebMarkupContainer container6 = new WebMarkupContainer("myContainer6");
    WebMarkupContainer container7 = new WebMarkupContainer("myContainer7");
    add(player1);
    
    add(container5);
    add(container6);
    add(container7);
    
    // to do always add current player here
    player1.add(new PlayerAreaPanel("panel1", new Player("")));
//    container4.setOutputMarkupId(true);
//    container4.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    container4.setVisible(false);
    container5.setVisible(false);
    container6.setVisible(false);
    container7.setVisible(false);
    if (players == 4) {
    	// to do add other players of current game
      container4.add(new PlayerAreaPanel("panel4-2", new Player("")));
      container4.add(new PlayerAreaPanel("panel4-3", new Player("")));
      container4.add(new PlayerAreaPanel("panel4-4", new Player("")));
      container4.setVisible(true);
    }
    if (players == 5) {
    	// to do add other players of current game
      container5.add(new PlayerAreaPanel("panel5-2", new Player("")));
      container5.add(new PlayerAreaPanel("panel5-3", new Player("")));
      container5.add(new PlayerAreaPanel("panel5-4", new Player("")));
      container5.add(new PlayerAreaPanel("panel5-5", new Player("")));
      container5.setVisible(true);
    }
    if (players == 6) {
    	// to do add other players of current game
      container6.add(new PlayerAreaPanel("panel6-2", new Player("")));
      container6.add(new PlayerAreaPanel("panel6-3", new Player("")));
      container6.add(new PlayerAreaPanel("panel6-4", new Player("")));
      container6.add(new PlayerAreaPanel("panel6-5", new Player("")));
      container6.add(new PlayerAreaPanel("panel6-6", new Player("")));
      container6.setVisible(true);
    }
    if (players == 7) {
    	// to do add other players of current game
      container7.add(new PlayerAreaPanel("panel7-2", new Player("")));
      container7.add(new PlayerAreaPanel("panel7-3", new Player("")));
      container7.add(new PlayerAreaPanel("panel7-4", new Player("")));
      container7.add(new PlayerAreaPanel("panel7-5", new Player("")));
      container7.add(new PlayerAreaPanel("panel7-6", new Player("")));
      container7.add(new PlayerAreaPanel("panel7-7", new Player("")));
      container7.setVisible(true);
    }    
    add(container4);
    
  }
  
  
}

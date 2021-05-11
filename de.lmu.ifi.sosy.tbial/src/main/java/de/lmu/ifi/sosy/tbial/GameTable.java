package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.GameManager;
import de.lmu.ifi.sosy.tbial.game.Player;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

 
  public GameTable() {

    // for testing until start game and join game isn't done
    Game test = getGameManager().getCurrentGames().get("test");
    test.addNewPlayer("sophia");
    test.addNewPlayer("teresa");
    test.addNewPlayer("david");
    test.addNewPlayer("ulla");
    
    int NumberOfPlayers = 4;
    //    		test.getMaxPlayers();

    add(new Label("gameName", test.getName()));
    WebMarkupContainer player1 = new WebMarkupContainer("player1");
    WebMarkupContainer container4 = new WebMarkupContainer("myContainer4");
    WebMarkupContainer container5 = new WebMarkupContainer("myContainer5");
    WebMarkupContainer container6 = new WebMarkupContainer("myContainer6");
    WebMarkupContainer container7 = new WebMarkupContainer("myContainer7");
    add(player1);
    
    add(container5);
    add(container6);
    add(container7);

    // for testing purposes
    Map<String, Player> currentPlayers = test.getPlayers();
    // always add current player here
    player1.add(
        new PlayerAreaPanel("panel1", currentPlayers.get(getSession().getUser().getName())));
    currentPlayers.remove(getSession().getUser().getName());
    // get the rest of the players
    ArrayList<Player> otherPlayers = new ArrayList<Player>(currentPlayers.values());
//    container4.setOutputMarkupId(true);
//    container4.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    container4.setVisible(false);
    container5.setVisible(false);
    container6.setVisible(false);
    container7.setVisible(false);
    if (NumberOfPlayers == 4) {
      // to do add other players of current game
      container4.add(new PlayerAreaPanel("panel4-2", otherPlayers.get(0)));
      container4.add(new PlayerAreaPanel("panel4-3", otherPlayers.get(1)));
      container4.add(new PlayerAreaPanel("panel4-4", otherPlayers.get(2)));
      container4.setVisible(true);
    }
    if (NumberOfPlayers == 5) {
      // to do add other players of current game
      container5.add(new PlayerAreaPanel("panel5-2", new Player("test1")));
      container5.add(new PlayerAreaPanel("panel5-3", new Player("test2")));
      container5.add(new PlayerAreaPanel("panel5-4", new Player("test3")));
      container5.add(new PlayerAreaPanel("panel5-5", new Player("test4")));
      container5.setVisible(true);
    }
    if (NumberOfPlayers == 6) {
      // to do add other players of current game
      container6.add(new PlayerAreaPanel("panel6-2", new Player("test1")));
      container6.add(new PlayerAreaPanel("panel6-3", new Player("test2")));
      container6.add(new PlayerAreaPanel("panel6-4", new Player("test3")));
      container6.add(new PlayerAreaPanel("panel6-5", new Player("test4")));
      container6.add(new PlayerAreaPanel("panel6-6", new Player("test5")));
      container6.setVisible(true);
    }
    if (NumberOfPlayers == 7) {
      // to do add other players of current game
      container7.add(new PlayerAreaPanel("panel7-2", new Player("test1")));
      container7.add(new PlayerAreaPanel("panel7-3", new Player("test2")));
      container7.add(new PlayerAreaPanel("panel7-4", new Player("test3")));
      container7.add(new PlayerAreaPanel("panel7-5", new Player("test4")));
      container7.add(new PlayerAreaPanel("panel7-6", new Player("test5")));
      container7.add(new PlayerAreaPanel("panel7-7", new Player("test6")));
      container7.setVisible(true);
    }    
    add(container4);
    
  }
  
  
}

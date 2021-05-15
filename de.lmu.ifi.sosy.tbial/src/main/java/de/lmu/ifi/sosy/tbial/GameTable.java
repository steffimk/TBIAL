package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;


  public GameTable() {

    // get current game
    Game current = getSession().getCurrentGame();
    
    // get number of players in current game
    int NumberOfPlayers = current.getCurrentNumberOfPlayers();

    add(new Label("gameName", current.getName()));
    
    // get all players of the game
    Map<String, Player> currentPlayers = current.getPlayers();
    // always add current session player here
    WebMarkupContainer player1 = new WebMarkupContainer("player1");
    add(player1);
    player1.add(
        new PlayerAreaPanel(
            "panel1", Model.of(currentPlayers.get(getSession().getUser().getName()))));

    // get the rest of the players
    ArrayList<Player> otherPlayers = new ArrayList<Player>();
    for (Map.Entry<String, Player> entry : currentPlayers.entrySet()) {
      if (!entry.getValue().getUserName().equals(getSession().getUser().getName())) {
        System.out.println(entry.getValue().getUserName());
        otherPlayers.add(entry.getValue());
      }
    }
    
    // add player areas of other players to game table
    IModel<List<Player>> playerModel = (IModel<List<Player>>) () -> otherPlayers;

    ListView<Player> playerList =
        new PropertyListView<>("container", playerModel) {
          private static final long serialVersionUID = 1L;

          int i = 2;

          @Override
          protected void populateItem(final ListItem<Player> listItem) {
            final Player player = listItem.getModelObject();
            PlayerAreaPanel panel = new PlayerAreaPanel("panel", Model.of(player));
            // add css classes
            listItem.add(
                new AttributeModifier("class", "player-" + "" + NumberOfPlayers + "-" + "" + (i)));
            panel.add(
                new AttributeModifier(
                    "class", "container" + "" + NumberOfPlayers + "-" + "" + (i)));
            listItem.add(panel);
            i++;
            if (i > NumberOfPlayers) {
              i = 2;
            }
          }
        };

    add(playerList);
    
  }
}

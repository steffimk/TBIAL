package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.StackCard;

/** Game Table */
@AuthenticationRequired
public class GameTable extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private WebMarkupContainer table;

  public GameTable() {

    table = new WebMarkupContainer("table");
    table.setOutputMarkupId(true);
    // get current game
    Game currentGame = getSession().getCurrentGame();

    // get number of players in current game
    int numberOfPlayers = currentGame.getCurrentNumberOfPlayers();

    // get username of current session player
    String currentPlayerUsername = getSession().getUser().getName();

    add(new Label("gameName", currentGame.getName()));
    
    // get all players of the game
    Map<String, Player> currentPlayers = currentGame.getPlayers();

    // set current session player as base player
    Player basePlayer = currentPlayers.get(currentPlayerUsername);

    // always add current session player here
    WebMarkupContainer player1 = new WebMarkupContainer("player1");

    player1.add(new PlayerAreaPanel("panel1", () -> basePlayer, currentGame, basePlayer, table));
    player1.setOutputMarkupId(true);
    // get the rest of the players
    ArrayList<Player> otherPlayers = new ArrayList<Player>();
    for (Map.Entry<String, Player> entry : currentPlayers.entrySet()) {
      if (!entry.getKey().equals(currentPlayerUsername)) {
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
            PlayerAreaPanel panel =
                new PlayerAreaPanel("panel", Model.of(player), currentGame, basePlayer, table);
            // add css classes
            listItem.add(
                new AttributeModifier("class", "player-" + "" + numberOfPlayers + "-" + "" + (i)));
            panel.add(
                new AttributeModifier(
                    "class", "container" + "" + numberOfPlayers + "-" + "" + (i)));
            listItem.add(panel);
            i++;
            if (i > numberOfPlayers) {
              i = 2;
            }
          }
        };

    WebMarkupContainer stackContainer = new WebMarkupContainer("stackContainer");
    stackContainer.add(
        new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            System.out.println("Clicked on stack");
            // TODO: currentGame.drawCardFromStack(basePlayer);
            target.add(table);
          }
        });

    Image stackImage = new Image("stackCard", PlayerAreaPanel.cardBackSideImage);
    stackContainer.add(stackImage);

    WebMarkupContainer heapContainer = new WebMarkupContainer("heapContainer");
    Image heapImage = new Image("heapCard", PlayerAreaPanel.cardBackSideImage);
    heapImage.setOutputMarkupId(true);
    heapContainer.add(heapImage);

    heapContainer.add(
        new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            boolean success = currentGame.clickedOnHeap(basePlayer);
            if (!success) return;
            
            StackCard topCardOfHeap = currentGame.getStackAndHeap().getUppermostCardOfHeap();
            if (topCardOfHeap != null) {
              heapImage.setImageResourceReference(
                  new PackageResourceReference(getClass(), topCardOfHeap.getResourceFileName()));
              heapImage.add(
                  new AttributeModifier(
                      "style",
                      "animation-name: discardAnimation;")); // TODO: make own animation for each
                                                             // player
            }
            target.add(table);
          }
        });

    AjaxLink<Void> discardButton =
        new AjaxLink<>("discardButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnDiscardButton(basePlayer);
          }
        };

    AjaxLink<Void> endTurnButton =
        new AjaxLink<>("endTurnButton") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            currentGame.clickedOnEndTurnButton(basePlayer);
          }
        };

    table.add(stackContainer);
    table.add(heapContainer);
    table.add(player1);
    table.add(playerList);
    add(table);
    add(discardButton);
    add(endTurnButton);
 }
}

package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;

public class GameLobbyTest extends PageTestBase {

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    Game game = new Game("gamename", 4, true, "123456", "testuser");
    // Add players so that game can be started
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    getGameManager().addGame(game);
  }

  @Test
  public void lobbyPageHasEmptyForm() {
    tester.startPage(GameLobby.class);
    tester.assertComponent("isHostLabel", Label.class);
    tester.assertComponent("startGameForm:startGameLink", Link.class);
    tester.assertComponent("currentStatusLabel", Label.class);
  }

  @Test
  public void startGameOk() {
    GameLobby gameLobby = tester.startPage(GameLobby.class);
    attemptStartGame();

    Game game = gameLobby.getGame();
    assertEquals(game.hasStarted(), true);
    assertEquals(game.hasStarted(), true);
    assertNotNull(game.getStackAndHeap());
    game.getPlayers()
        .values()
        .forEach(
            v -> {
              assertNotNull(v.getRoleCard());
              assertNotNull(v.getCharacterCard());
              assertEquals(v.getPrestigeInt(), 0);
              assertEquals(v.getHandCards().size(), v.getMentalHealthInt());
              if (v.getRole() == Role.MANAGER) {
                assertEquals(v.getMentalHealthInt(), v.getCharacterCard().getMaxHealthPoints() + 1);
              } else {
                assertEquals(v.getMentalHealthInt(), v.getCharacterCard().getMaxHealthPoints());
              }
            });

    tester.assertRenderedPage(GameTable.class);
  }

  private void attemptStartGame() {
    // start and render the test page
    tester.startPage(GameLobby.class);
    // assert rendered page class
    tester.assertRenderedPage(GameLobby.class);
    tester.clickLink("startGameForm:startGameLink");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void chatIsWorking() {
	  GameLobby gameLobby = tester.startPage(GameLobby.class);

    // check if chat is empty in the beginning
    assertEquals(
        gameLobby.getGameManager().getGameOfUser("testuser").getChatMessages().isEmpty(), true);

    // basePlayer sends two messages
    FormTester form = tester.newFormTester("chatPanel:form");
    form.setValue("message", "TestText1");
    form.submit("send");
    form.setValue("message", "TestText2");
    form.submit("send");

    // basePlayer sends an empty message
    form.setValue("message", "");
    form.submit("send");

    // check if non-empty messages are displayed
    ListView<ChatMessage> messageList =
        (ListView<ChatMessage>)
            tester.getComponentFromLastRenderedPage("chatPanel:chatMessages:messages");

    messageList.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<ChatMessage>, Void>() {

          @Override
          public void component(ListItem<ChatMessage> item, IVisit<Void> visit) {
            tester.assertComponent(item.getPath().substring(2) + ":sender", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":sender", item.getModelObject().getSender());
            tester.assertComponent(item.getPath().substring(2) + ":textMessage", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":textMessage",
                item.getModelObject().getTextMessage());
            visit.dontGoDeeper();
          }
        });

    // check if empty messages were not sent
    assertEquals(gameLobby.getGameManager().getGameOfUser("testuser").getChatMessages().size(), 2);
  }
}

package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

  Game game;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    game = new Game("gamename", 4, true, "123456", "testuser");
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
  public void leaveGameOk() {
    tester.startPage(GameLobby.class);
    tester.assertRenderedPage(GameLobby.class);
    Game game = getGameManager().getGameOfUser("testuser");
    assertNotNull(game);

    tester.submitForm("leaveForm");
    tester.assertRenderedPage(Lobby.class);

    assertNull(game.getPlayers().get("testuser"));
    assertNull(getGameManager().getGameOfUser("testuser"));

    assertNotNull(getGameManager().getCurrentGames().containsKey("gamename"));
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
                assertEquals(
                    v.getMentalHealthInt(), 5 /*v.getCharacterCard().getMaxHealthPoints() * +1*/);
              } else {
                assertEquals(
                    v.getMentalHealthInt(), 4 /*v.getCharacterCard().getMaxHealthPoints()*/);
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
            tester.assertVisible(item.getPath().substring(2) + ":sender");
            tester.assertVisible(item.getPath().substring(2) + ":textMessage");
            visit.dontGoDeeper();
          }
        });

    // check if empty messages were not sent
    assertEquals(gameLobby.getGameManager().getGameOfUser("testuser").getChatMessages().size(), 2);
  }

  @Test
  public void whisperIsWorking() {
    GameLobby gameLobby = tester.startPage(GameLobby.class);
    // basePlayer sends private message to A
    FormTester form = tester.newFormTester("chatPanel:form");
    form.setValue("message", "/w A hallo");
    form.submit("send");

    // check that private message gets displayed
    @SuppressWarnings("unchecked")
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
                item.getPath().substring(2) + ":sender",
                item.getModelObject().getPureSender()
                    + " to "
                    + item.getModelObject().getReceiver()
                    + ": ");
            tester.assertComponent(item.getPath().substring(2) + ":textMessage", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":textMessage",
                item.getModelObject().getTextMessage());
            tester.assertVisible(item.getPath().substring(2) + ":sender");
            tester.assertVisible(item.getPath().substring(2) + ":textMessage");
            visit.dontGoDeeper();
          }
        });
  }

  @Test
  public void replyIsWorking() {
    GameLobby gameLobby = tester.startPage(GameLobby.class);
    game.getChatMessages().add(new ChatMessage("A", "hallo", true, "testuser"));
    // basePlayer responds to private message from A
    FormTester form = tester.newFormTester("chatPanel:form");
    form.setValue("message", "/r hallo");
    form.submit("send");

    // check that private message gets displayed
    @SuppressWarnings("unchecked")
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
                item.getPath().substring(2) + ":sender",
                item.getModelObject().getPureSender()
                    + " to "
                    + item.getModelObject().getReceiver()
                    + ": ");
            tester.assertComponent(item.getPath().substring(2) + ":textMessage", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":textMessage",
                item.getModelObject().getTextMessage());
            tester.assertVisible(item.getPath().substring(2) + ":sender");
            tester.assertVisible(item.getPath().substring(2) + ":textMessage");
            visit.dontGoDeeper();
          }
        });
  }

  @Test
  public void privateMessageBetweenOtherPlayersDoesNotGetDisplayed() {
    GameLobby gameLobby = tester.startPage(GameLobby.class);
    game.getChatMessages().add(new ChatMessage("A", "hallo", true, "B"));

    // check that private message which is not directed at basePlayer does not get displayed
    @SuppressWarnings("unchecked")
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
                item.getPath().substring(2) + ":sender",
                item.getModelObject().getPureSender()
                    + " to "
                    + item.getModelObject().getReceiver()
                    + ": ");
            tester.assertComponent(item.getPath().substring(2) + ":textMessage", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":textMessage",
                item.getModelObject().getTextMessage());
            tester.assertInvisible(item.getPath().substring(2) + ":sender");
            tester.assertInvisible(item.getPath().substring(2) + ":textMessage");
            visit.dontGoDeeper();
          }
        });
  }
}

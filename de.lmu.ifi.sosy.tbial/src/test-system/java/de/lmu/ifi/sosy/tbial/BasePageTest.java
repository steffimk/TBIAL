package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

public class BasePageTest extends PageTestBase {

  User testuser;
  User testuser2;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    testuser = database.getUser("testuser");
    database.register("testuser2", "testpassword2");
    getSession().authenticate("testuser2", "testpassword2");
    testuser2 = database.getUser("testuser2");
    Game game = new Game("gamename", 4, true, "123456", "testuser");
    testuser2.invite(new Invitation("testuser", "has invited you to join a game.", game.getName()));
    getGameManager().addGame(game);
  }

  @Test
  public void displayMessageContainer() {
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Lobby.class);

    TBIALSession session = getSession();
    session.setUser(testuser);

    tester.assertComponent("messageContainer", WebMarkupContainer.class);
    tester.assertComponent("messageContainer:message", AjaxLink.class);
    tester.assertComponent("messageContainer:numberOfMessages", Label.class);
    tester.assertModelValue("messageContainer:numberOfMessages", 0);
    tester.assertComponent("modal", ModalWindow.class);
  }

  @Test
  public void receiveMessage() {
    displayMessageContainer();
    testuser.invite(new Invitation("testuser2", "has invited you to join a game.", "game2"));
    tester.assertModelValue("messageContainer:numberOfMessages", 1);
  }

  @Test
  public void acceptInvitation() {
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Lobby.class);

    Game game = getGameManager().getGameOfUser("testuser");

    tester.clickLink("messageContainer:message");
    tester.assertComponent("modal:content", NotificationPanel.class);
    FormTester acceptForm =
        tester.newFormTester("modal:content:notificationContainer:0:notificationForm:accept");
    tester.assertModelValue(
        "modal:content:notificationContainer:0:notificationForm:notificationSender", "testuser");
    tester.assertModelValue(
        "modal:content:notificationContainer:0:notificationForm:notificationMessage",
        " has invited you to join a game.");
    acceptForm.submit("acceptButton");
    assertEquals(testuser2.getInvitations().size(), 0);
    assertEquals(game.getChatMessages().get(0).getSender(), "UPDATE: ");
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(), "testuser2 accepted the game invitation.");
    tester.assertRenderedPage(GameLobby.class);
  }

  @Test
  public void rejectInvitation() {
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Lobby.class);

    Game game = getGameManager().getGameOfUser("testuser");

    tester.clickLink("messageContainer:message");
    tester.assertComponent("modal:content", NotificationPanel.class);
    FormTester rejectForm =
        tester.newFormTester("modal:content:notificationContainer:0:notificationForm:reject");
    tester.assertModelValue(
        "modal:content:notificationContainer:0:notificationForm:notificationSender", "testuser");
    tester.assertModelValue(
        "modal:content:notificationContainer:0:notificationForm:notificationMessage",
        " has invited you to join a game.");
    rejectForm.submit("rejectButton");
    assertEquals(testuser2.getInvitations().size(), 0);
    assertEquals(game.getChatMessages().get(0).getSender(), "UPDATE: ");
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(), "testuser2 rejected the game invitation.");
    tester.assertRenderedPage(Lobby.class);
  }
}
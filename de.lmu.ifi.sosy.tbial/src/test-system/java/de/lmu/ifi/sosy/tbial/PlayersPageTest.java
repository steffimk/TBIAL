package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

public class PlayersPageTest extends PageTestBase {

  User testuser;
  User testuser2;
  User testuser3;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    testuser = database.getUser("testuser");
    database.register("testuser2", "testpassword2");
    testuser2 = database.getUser("testuser2");
    getSession().authenticate("testuser2", "testpassword2");
    database.register("testuser3", "testpassword3");
    testuser3 = database.getUser("testuser3");
    getSession().authenticate("testuser3", "testpassword3");
    Game game = new Game("gamename", 4, true, "123456", "testuser");
    getGameManager().addGame(game);
    getGameManager().joinGame(testuser2.getName(), game, "123456");
  }

  @Test
  public void navigateToCreateNewGame() {
    tester.startPage(PlayersPage.class);
    tester.assertRenderedPage(PlayersPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("createGameButton");
    tester.assertRenderedPage(Lobby.class);
  }

  @Test
  public void navigateToGamesPage() {
    tester.startPage(PlayersPage.class);
    tester.assertRenderedPage(PlayersPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showGamesButton");
    tester.assertRenderedPage(GamesPage.class);
  }

  @Test
  public void navigateToPlayersPage() {
    tester.startPage(PlayersPage.class);
    tester.assertRenderedPage(PlayersPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showPlayersButton");
    tester.assertRenderedPage(PlayersPage.class);
  }

  @Test
  public void displayUsersInLobbyAndSendInvitationToGame() {
    TBIALSession session = getSession();
    session.setUser(testuser);

    tester.startPage(PlayersPage.class);
    tester.assertRenderedPage(PlayersPage.class);
    tester.assertComponent("playerlistContainer", WebMarkupContainer.class);
    @SuppressWarnings("unchecked")
    ListView<User> playerList =
        (ListView<User>)
            tester.getComponentFromLastRenderedPage("playerlistContainer:loggedInUsers");

    playerList.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<User>, Void>() {

          @Override
          public void component(ListItem<User> item, IVisit<Void> visit) {
            User user = item.getModelObject();

            tester.assertComponent(item.getPath().substring(2) + ":name", Label.class);
            tester.assertModelValue(item.getPath().substring(2) + ":name", user.getName());
            FormTester form = tester.newFormTester(item.getPath().substring(2) + ":invitationForm");
            // invite button invisible for oneself
            if (user.getName() == "testuser" && session.getUser() == testuser) {
              tester.isInvisible(item.getPath().substring(2) + ":invitationForm:inviteButton");
            }
            // invite button disabled for player who already joined game
            if (user.getName() == "testuser2" && session.getUser() == testuser) {
              tester.isDisabled(item.getPath().substring(2) + ":invitationForm:inviteButton");
            }
            // invite button visible for player who hasn't joined game; sends invitation
            if (user.getName() == "testuser3" && session.getUser() == testuser) {
              tester.isEnabled(item.getPath().substring(2) + ":invitationForm:inviteButton");
              form.submit("inviteButton");
            }
          }
        });
    assertEquals(testuser2.getInvitations().size(), 0);
    assertEquals(testuser3.getInvitations().get(0).getSender(), "testuser");
    assertEquals(
        testuser3.getInvitations().get(0).getTextMessage(), " has invited you to join a game.");
  }
}

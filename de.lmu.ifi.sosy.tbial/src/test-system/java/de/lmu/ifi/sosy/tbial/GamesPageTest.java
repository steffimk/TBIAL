package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

public class GamesPageTest extends PageTestBase {

  User testuser;
  User testuser2;
  User testuser3;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    testuser = database.getUser("testuser");
    database.register("testuser2", "testpassword");
    getSession().authenticate("testuser2", "testpassword");
    testuser = database.getUser("testuser2");
    database.register("testuser3", "testpassword");
    getSession().authenticate("testuser3", "testpassword");
    testuser = database.getUser("testuser3");
    Game game = new Game("gamename", 4, true, "123456", "testuser");
    Game game2 = new Game("gamename2", 4, true, "123456", "testuser2");
    getGameManager().addGame(game);
    getGameManager().addGame(game2);
  }

  @Test
  public void testJoinGame() {
    TBIALSession session = getSession();
    session.setUser(testuser);
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);
    tester.assertComponent("gameListContainer", WebMarkupContainer.class);

    @SuppressWarnings("unchecked")
    ListView<Game> gameList =
        (ListView<Game>) tester.getComponentFromLastRenderedPage("gameListContainer:openGames");

    gameList.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<Game>, Void>() {

          @Override
          public void component(ListItem<Game> item, IVisit<Void> visit) {
            Game game = item.getModelObject();
            FormTester form = tester.newFormTester(item.getPath().substring(2) + ":joinGameForm");
            // join Button navigates back to game Lobby if the button is used on the current game
            if (session.getUser().getName() == "testuser" && game.getName() == "gamename") {
              form.submit(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              tester.assertRenderedPage(GameLobby.class);
            }
            // join Button is disabled if user wants to join another game
            if (session.getUser().getName() == "testuser" && game.getName() == "gamename2") {
              tester.isDisabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
            }
            // join Button navigates to the games Lobby if not in game
            /*if (session.getUser().getName() == "testuser3" && game.getName() == "gamename") {
              form.submit(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              tester.assertRenderedPage(GameLobby.class);
            }*/
          }
        });
  }
}

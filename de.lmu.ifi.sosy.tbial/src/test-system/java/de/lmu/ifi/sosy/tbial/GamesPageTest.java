package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

public class GamesPageTest extends PageTestBase {

  User testuser;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    testuser = database.getUser("testuser");
    Game game = new Game("gamename", 4, true, "123456", "testuser");
    getGameManager().addGame(game);
  }

  @Test
  public void tryToJoinTheGame() {
    TBIALSession session = getSession();
    session.setUser(testuser);

    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);
    tester.assertComponent("gameListContainer", WebMarkupContainer.class);
  }
}

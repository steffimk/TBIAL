package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.game.Game;

import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/** Tests with WicketTester if all game data is displayed */
public class ShowGamesTest extends PageTestBase {

  List<Game> gameList;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    database.register("testuser2", "pw2");
    database.register("testuser3", "pw3");
    getSession().authenticate("testuser", "testpassword");
    getSession().authenticate("testuser2", "pw2");
    getSession().authenticate("testuser3", "pw3");

    gameList =
        new ArrayList<Game>() {
          private static final long serialVersionUID = 1L;
          {
            add(new Game("game1", 4, true, "123456", "testuser"));
            add(new Game("game2", 5, false, "", "testuser2"));
            add(new Game("game3", 7, false, "", "testuser3"));
          }
        };

    for (Game game : gameList) {
      getSession().getTbialApplication().getGameManager().addGame(game);
    }
  }

  @Test
  public void numberOfCreatedGamesCorrect() {
    tester.startPage(Lobby.class);
    
    String htmlDocument = tester.getLastResponse().getDocument();
    List<TagTester> tagTesterList =
        TagTester.createTagsByAttribute(htmlDocument, "wicket:id", "gamename", false);
    Assert.assertEquals(3, tagTesterList.size());
    
    tester.assertRenderedPage(Lobby.class);
  }

  @Test
  public void displayCreatedGamesInLobby() {
	String htmlDocument = tester.getLastResponse().getDocument();
    List<TagTester> tagTesterList = TagTester.createTagsByAttribute(htmlDocument, "wicket:id", "gamename", false);
    List<String> gameNames = new ArrayList<String>();
    List<String> tagTesterValues = new ArrayList<String>();

    Assert.assertEquals(gameList.size(), tagTesterList.size());

    for (int i = 0; i < gameList.size(); i++) {
      gameNames.add(gameList.get(i).getName());
      tagTesterValues.add(tagTesterList.get(i).getValue());
    }

    for (String gameName : gameNames) {
      Assert.assertTrue(tagTesterValues.contains(gameName));
    }
  }
}

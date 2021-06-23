package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Simple test using the WicketTester */
public class LobbyTest extends PageTestBase {
  List<Game> gameList;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    database.register("testuser2", "testpassword2");
    getSession().authenticate("testuser2", "pw2");
    database.register("testuser3", "testpassword3");
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
  public void lobbyPageHasEmptyForm() {
    tester.startPage(Lobby.class);
    tester.assertComponent("newGameForm:newGameName", TextField.class);
    tester.assertComponent("newGameForm:maxPlayers", NumberTextField.class);
    tester.assertComponent("newGameForm:isPrivate", AjaxCheckBox.class);
    tester.assertInvisible("newGameForm:passwordContainer");
    tester.assertModelValue("newGameForm:maxPlayers", 4);
    tester.assertModelValue("newGameForm:isPrivate", false);
  }

  @Test
  public void createGameOk() {
    Lobby lobby = tester.startPage(Lobby.class);

    User user = getSession().getUser();
    getGameManager().removeUserFromGame(user.getName());

    attemptCreateNewPublicGame("newGame", "5");

    Game game = getGameManager().getGameOfUser(getSession().getUser().getName());
    assertNotNull(game);

    assertEquals(lobby.getGameManager().getCurrentGames().containsValue(game), true);

    tester.assertRenderedPage(GameLobby.class);
  }

  private void attemptCreateNewPublicGame(String gameName, String maxPlayers) {
    // start and render the test page
    tester.startPage(Lobby.class);

    // assert rendered page class
    tester.assertRenderedPage(Lobby.class);

    FormTester form = tester.newFormTester("newGameForm");
    form.setValue("newGameName", gameName);
    form.setValue("maxPlayers", maxPlayers);
    form.setValue("isPrivate", false);
    form.submit("newGameButton");
  }

  @Test
  public void numberOfCreatedGamesCorrect() {
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Lobby.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showGamesButton");

    String htmlDocument = tester.getLastResponse().getDocument();
    System.out.println(htmlDocument);
    List<TagTester> tagTesterList =
        TagTester.createTagsByAttribute(htmlDocument, "wicket:id", "gamename", false);
    Assert.assertEquals(3, tagTesterList.size());
  }

  @Test
  public void displayCreatedGamesInLobby() {
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Lobby.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showGamesButton");

    String htmlDocument = tester.getLastResponse().getDocument();
    List<TagTester> tagTesterList =
        TagTester.createTagsByAttribute(htmlDocument, "wicket:id", "gamename", false);
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

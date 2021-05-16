package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import de.lmu.ifi.sosy.tbial.game.Game;

import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

/** Simple test using the WicketTester */
public class LobbyTest extends PageTestBase {
	
  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
  }

  @Test
  public void lobbyPageHasEmptyForm() {
    tester.startPage(Lobby.class);
    tester.assertComponent("newGameForm:newGameName", TextField.class);
    tester.assertComponent("newGameForm:maxPlayers", NumberTextField.class);
    tester.assertComponent("newGameForm:isPrivate", AjaxCheckBox.class);
    tester.assertComponent("newGameForm:passwordContainer:newGamePw", PasswordTextField.class);
    tester.assertModelValue("newGameForm:maxPlayers", 4);
    tester.assertModelValue("newGameForm:isPrivate", true);
    tester.assertModelValue("newGameForm:passwordContainer:newGamePw", null);
  }

  @Test
  public void createGameOk() {
    Lobby lobby = tester.startPage(Lobby.class);
    attemptCreateNewGame("newGame", "5", true, "testPassword");

    TBIALSession session = getSession();

    Game game = session.getCurrentGame();
    assertNotNull(game);

    assertEquals(lobby.getGameManager().getCurrentGames().containsValue(game), true);

    tester.assertRenderedPage(GameLobby.class);
  }

  private void attemptCreateNewGame(
      String gameName, String maxPlayers, boolean isPrivate, String password) {
    // start and render the test page
    tester.startPage(Lobby.class);

    // assert rendered page class
    tester.assertRenderedPage(Lobby.class);

    FormTester form = tester.newFormTester("newGameForm");
    form.setValue("newGameName", gameName);
    form.setValue("maxPlayers", maxPlayers);
    form.setValue("isPrivate", true);
    form.setValue("passwordContainer:newGamePw", password);
    form.submit("newGameButton");
  }
  
}

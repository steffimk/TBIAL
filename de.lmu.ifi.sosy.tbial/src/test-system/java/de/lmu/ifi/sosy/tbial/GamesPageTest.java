package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
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
  User testuser4;
  Game game;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    testuser = database.getUser("testuser");
    database.register("testuser2", "testpassword");
    getSession().authenticate("testuser2", "testpassword");
    testuser2 = database.getUser("testuser2");
    database.register("testuser3", "testpassword");
    getSession().authenticate("testuser3", "testpassword");
    testuser3 = database.getUser("testuser3");
    database.register("testuser4", "testpassword");
    getSession().authenticate("testuser4", "testpassword");
    testuser4 = database.getUser("testuser4");
    game = new Game("gamename", 4, false, null, "testuser");
    Game game2 = new Game("gamename2", 4, true, "123456", "testuser2");
    getGameManager().addGame(game);
    getGameManager().addGame(game2);
  }

  @Test
  public void hasComponents() {
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

            tester.assertComponent(item.getPath().substring(2) + ":gamename", Label.class);
            tester.assertModelValue(item.getPath().substring(2) + ":gamename", game.getName());
            tester.assertComponent(item.getPath().substring(2) + ":numberOfPlayers", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":numberOfPlayers",
                game.getCurrentNumberOfPlayers() + "/" + game.getMaxPlayers());
            if (game.isPrivate()) {
              tester.assertComponent(
                  item.getPath().substring(2) + ":lockedIcon", WebMarkupContainer.class);
              tester.assertComponent(
                  item.getPath().substring(2) + ":joinGameForm:joinGamePw",
                  PasswordTextField.class);
              tester.isVisible(item.getPath().substring(2) + ":lockedIcon");
              tester.isVisible(item.getPath().substring(2) + ":joinGameForm:joinGamePw");
            } else {
              tester.assertComponent(
                  item.getPath().substring(2) + ":unlockedIcon", WebMarkupContainer.class);
              tester.isVisible(item.getPath().substring(2) + ":unlockedIcon");
            }
          }
        });
  }

  @Test
  public void navigateBackToGameLobby() {
    TBIALSession session = getSession();
    session.setUser(testuser);
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

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
            // join button navigates back to game lobby if the button is used on the current game
            if (session.getUser() == testuser && game.getName() == "gamename") {
              tester.isEnabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              form.submit("joinGameButton");
              tester.assertRenderedPage(GameLobby.class);
            }
            // join button is disabled if user wants to join another game
            if (session.getUser() == testuser && game.getName() == "gamename2") {
              tester.isDisabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
            }
          }
        });
  }

  @Test
  public void joinPublicGame() {
    TBIALSession session = getSession();
    session.setUser(testuser3);
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

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
            // join button navigates to the game's lobby if not in game
            if (session.getUser() == testuser3 && game.getName() == "gamename") {
              tester.isEnabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              form.submit("joinGameButton");
              tester.assertRenderedPage(GameLobby.class);
              assertTrue(game.getInGamePlayerNames().contains("testuser3"));
            }
          }
        });
  }

  @Test
  public void joinFullGameNotPossible() {
    TBIALSession session = getSession();
    session.setUser(testuser4);
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

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
            // join button navigates to lobby if game is already full
            if (session.getUser() == testuser4 && game.getName() == "gamename") {
              tester.isEnabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              form.submit("joinGameButton");
              assertFalse(game.getInGamePlayerNames().contains("testuser4"));
            }
          }
        });
  }

  @Test
  public void joinPrivateGame() {
    TBIALSession session = getSession();
    session.setUser(testuser4);
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);
   
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
            // join button navigates to the game's lobby if not in game
            if (session.getUser() == testuser4 && game.getName() == "gamename2") {
              tester.isEnabled(item.getPath().substring(2) + ":joinGameForm:joinGameButton");
              form.setValue("joinGamePw", "123456");
              form.submit("joinGameButton");
              tester.assertRenderedPage(GameLobby.class);
              assertTrue(game.getInGamePlayerNames().contains("testuser4"));
              // need to switch back to GamesPage otherwise the asserting of the list components of
              // the next game will fail because you were navigated to the GameLobby
              tester.startPage(GamesPage.class);
            }
            visit.dontGoDeeper();
          }
        });
  }

  @Test
  public void navigateToCreateNewGame() {
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("createGameButton");
    tester.assertRenderedPage(Lobby.class);
  }

  @Test
  public void navigateToGamesPage() {
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showGamesButton");
    tester.assertRenderedPage(GamesPage.class);
  }

  @Test
  public void navigateToPlayersPage() {
    tester.startPage(GamesPage.class);
    tester.assertRenderedPage(GamesPage.class);

    FormTester form = tester.newFormTester("menuForm");
    form.submit("showPlayersButton");
    tester.assertRenderedPage(PlayersPage.class);
  }
}

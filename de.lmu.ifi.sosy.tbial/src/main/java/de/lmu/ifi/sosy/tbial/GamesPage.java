package de.lmu.ifi.sosy.tbial;

import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.game.Game;

/** This page shows a list of all current games and provides the option to join one of them. */
public class GamesPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public GamesPage() {

    Form<?> menuForm = new Form<>("menuForm");

    Button createGameButton =
        new Button("createGameButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getHomePage());
          }
        };
    menuForm.add(createGameButton);

    Button showGamesButton =
        new Button("showGamesButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getGamesPage());
          }
        };
    menuForm.add(showGamesButton);

    Button showPlayersButton =
        new Button("showPlayersButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getPlayersPage());
          }
        };
    menuForm.add(showPlayersButton);
    add(menuForm);

    IModel<List<Game>> gameModel =
        (IModel<List<Game>>) () -> getGameManager().getCurrentGamesAsList();

    ListView<Game> gameList =
        new PropertyListView<>("openGames", gameModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<Game> listItem) {
            final Game game = listItem.getModelObject();
            Form<?> joinGameForm = new Form<>("joinGameForm");
            PasswordTextField joinGamePw = new PasswordTextField("joinGamePw", new Model<>(""));
            joinGamePw.setVisible(game.isPrivate());
            Button joinGameButton =
                new Button("joinGameButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    joinGame(game, joinGamePw.getModelObject());
                  }
                };
            joinGameForm.add(joinGamePw);
            joinGameForm.add(joinGameButton);
            if (isUserNull()) throw new NullPointerException("Player is null!");

            if (game.getPlayers().containsKey(getUserName())) {
              listItem.add(new Label("currentGame", "X"));
            } else {
              listItem.add(new Label("currentGame", " "));
            }

            listItem.add(
                new Label(
                    "numberOfPlayers",
                    game.getCurrentNumberOfPlayers() + "/" + game.getMaxPlayers()));
            listItem.add(new Label("gamename", game.getName()));
            WebMarkupContainer lockedIcon = new WebMarkupContainer("lockedIcon");
            lockedIcon.setOutputMarkupId(true);
            lockedIcon.setVisible(game.isPrivate());
            listItem.add(lockedIcon);
            WebMarkupContainer unlockedIcon = new WebMarkupContainer("unlockedIcon");
            unlockedIcon.setVisible(!game.isPrivate());
            unlockedIcon.setOutputMarkupId(true);
            listItem.add(unlockedIcon);
            listItem.add(joinGameForm);
          }
        };

    WebMarkupContainer gameListContainer = new WebMarkupContainer("gameListContainer");
    gameListContainer.add(gameList);
    gameListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    gameListContainer.setOutputMarkupId(true);

    add(gameListContainer);
  }

  /**
   * Method to join a game, adding to the games player list, setting the current game and being
   * directed to its GameLobby
   *
   * @param game
   * @param password
   */
  public void joinGame(Game game, String password) {
    String username = getSession().getUser().getName();
    if (game.checkIfYouCanJoin(username, password)) {
      game.addNewPlayer(getSession().getUser().getName());
      getSession().setCurrentGame(game);
      setResponsePage(getTbialApplication().getGameLobbyPage());
    }
  }

  public boolean isUserNull() {
    if (getSession().getUser() != null) {
      return false;
    }
    return true;
  }

  public String getUserName() {
    return getSession().getUser().getName();
  }
}

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

public class GamesPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public GamesPage() {

    Form MenuForm = new Form("MenuForm");

    Button createGameButton =
        new Button("createGameButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getHomePage());
          }
        };
    MenuForm.add(createGameButton);

    Button showGamesButton =
        new Button("showGamesButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getGamesPage());
          }
        };
    MenuForm.add(showGamesButton);

    Button showPlayersButton =
        new Button("showPlayersButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getPlayersPage());
          }
        };
    MenuForm.add(showPlayersButton);
    add(MenuForm);

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
            listItem.add(
                new Label(
                    "numberOfPlayers",
                    game.getCurrentNumberOfPlayers() + "/" + game.getMaxPlayers()));
            listItem.add(new Label("name", game.getName()));
            listItem.add(new Label("access", game.isPrivate()));
            listItem.add(joinGameForm);
          }
        };

    WebMarkupContainer gameListContainer = new WebMarkupContainer("gameListContainer");
    gameListContainer.add(gameList);
    gameListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    gameListContainer.setOutputMarkupId(true);

    add(gameListContainer);
  }

  public void joinGame(Game game, String password) {
    String username = getSession().getUser().getName();
    if (checkIfYouCanJoin(game, username, password)) {
      game.addNewPlayer(getSession().getUser().getName());
      getSession().setCurrentGame(game);
        setResponsePage(getTbialApplication().getGameLobbyPage());
      }
  }

  public boolean checkIfYouCanJoin(Game game, String username, String password) {
    if (game.hasStarted()) {
      return false;
    }
    if (game.getCurrentNumberOfPlayers() >= game.getMaxPlayers()) {
      return false;
    }
    if (game.getInGamePlayers().containsKey(username)) {
    	return false;
    }
    if (game.isPrivate()
        && !game.getHash().equals(Game.getHashedPassword(password, game.getSalt()))) {
      return false;
    }
    return true;
  }
}

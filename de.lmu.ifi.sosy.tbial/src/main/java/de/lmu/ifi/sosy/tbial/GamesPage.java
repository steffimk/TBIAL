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

  final String userName = getSession().getUser().getName();

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
                    ((TBIALSession) getSession()).joinGame(game, joinGamePw.getModelObject());
                    setResponsePage(getTbialApplication().getGameLobbyPage());
                  }
                };
            joinGameForm.add(joinGamePw);
            joinGameForm.add(joinGameButton);
            if (game.getPlayers().containsKey(userName)) {
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
}

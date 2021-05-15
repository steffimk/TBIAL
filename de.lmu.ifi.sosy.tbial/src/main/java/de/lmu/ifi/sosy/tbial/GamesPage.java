package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.game.Game;

public class GamesPage extends BasePage {

  private static final long serialVersionUID = 1L;

  private String currentRadioValue;

  public GamesPage() {

    Form MenuForm =
        new Form("MenuForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            //info("Created Game");
            setResponsePage(getTbialApplication().getHomePage());
          }
        };

    Button showGamesButton =
        new Button("showGamesButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            info("Already in Game View!");
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

    Form joinForm = new Form("JoinForm");

    ListView<Game> gameList =
        new PropertyListView<>("openGames", gameModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<Game> listItem) {

            final Game game = listItem.getModelObject();
            listItem.add(new Label("name", game.getName()));
            listItem.add(
                new Label(
                    "numberOfPlayers",
                    game.getCurrentNumberOfPlayers() + "/" + game.getMaxPlayers()));
            listItem.add(new Label("access", game.isPrivate()));
          }
        };

    WebMarkupContainer gameListContainer = new WebMarkupContainer("gameListContainer");
    gameListContainer.add(gameList);
    gameListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    gameListContainer.setOutputMarkupId(true);

    add(gameListContainer);
    joinForm.add(gameListContainer);
    add(joinForm);

    add(new FeedbackPanel("feedback"));

    ArrayList<String> gameChoiceOptions = new ArrayList<>();
    for (int i = 0; i < getGameManager().getCurrentGamesAsList().size(); i++) {
      String currentGame = getGameManager().getCurrentGamesAsList().get(i).getName();
      gameChoiceOptions.add(currentGame);
    }

    RadioChoice<String> gameChoice =
        new RadioChoice<String>(
            "hosting", new PropertyModel<String>(this, "selected"), gameChoiceOptions);

    Form<?> JoinForm =
        new Form<Void>("form") {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onSubmit() {
            joinGame(currentRadioValue);
          }
        };

    add(JoinForm);
    JoinForm.add(gameChoice);
  }

  public void joinGame(String selected) {
    List<Game> games = getGameManager().getCurrentGamesAsList();
    Game selectedGame = null;
    for (int i = 0; i < games.size(); i++) {
      if (games.get(i).getName() == selected) {
        selectedGame = games.get(i);
      }
    }

    if (selectedGame.isPrivate()) {
      //TODO: Modal öffnen
    }
    if (selectedGame != null && !selectedGame.hasStarted()) {
      getSession().setCurrentGame(selectedGame);
      setResponsePage(getTbialApplication().getGameLobbyPage());
    }
  }
}

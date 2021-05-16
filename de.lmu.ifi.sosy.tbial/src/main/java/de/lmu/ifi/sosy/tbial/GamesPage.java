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

    Form joinForm = new Form("joinForm");

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
            "choice", new PropertyModel<String>(this, "currentRadioValue"), gameChoiceOptions);

    Form<?> chooseForm =
        new Form<Void>("chooseForm") {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onSubmit() {
            joinGame(currentRadioValue);
          }
        };

    add(chooseForm);
    chooseForm.add(gameChoice);
  }

  public void joinGame(String selected) {
    List<Game> games = getGameManager().getCurrentGamesAsList();
    Game selectedGame = null;
    for (int i = 0; i < games.size(); i++) {
      if (games.get(i).getName() == selected) {
        selectedGame = games.get(i);
      }
    }

    if (selectedGame != null) {
      if (selectedGame.isPrivate()) {

        //TODO: Modal öffnen
        // 1. Modal Klasse erstellen mit Titel und Beschreibung
        // 2. Modal anzeigen: modal.show(target);
        // 3. Falls kein Close --> Hinzufügen
        // 4. Falls kein Submit --> Hinzufügen
      }

      if (checkIfYouCanJoin(selectedGame)) {
        getSession().setCurrentGame(selectedGame);
        setResponsePage(getTbialApplication().getGameLobbyPage());
      }
    }
  }

  public boolean checkIfYouCanJoin(Game game) {
    if (game.hasStarted()) {
      return false;
    }
    if (game.getCurrentNumberOfPlayers() >= game.getMaxPlayers()) {
      return false;
    }
    return true;
  }
}

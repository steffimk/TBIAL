package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.Arrays;
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

  private static final List<String> TYPES =
      Arrays.asList(new String[] {"Shared Host", "VPS", "Dedicated Server"});

  //variable to hold radio button values
  private String selected = "VPS";
  //private final PasswordTextField passwordField;

  //private final Button pwJoinButton;

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
            //List SITES = null;
            //List<String> myRadioButtonList = new ArrayList<String>();

            /*for (int i = 0; i < getGameManager().getCurrentGamesAsList().size(); i++) {
              Game currentGame = getGameManager().getCurrentGamesAsList().get(i);
              SITES = Arrays.asList(new String[] {currentGame.getName()});
            }*/

            final Game game = listItem.getModelObject();
            /*listItem.add(
            new RadioChoice<String>(
                "radioValue", new PropertyModel<String>(this, "selected"), SITES));*/
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

    ArrayList<String> goList = new ArrayList<>();
    for (int i = 0; i < getGameManager().getCurrentGamesAsList().size(); i++) {
      String currentGame = getGameManager().getCurrentGamesAsList().get(i).getName();
      System.out.println("Current game: ");
      System.out.println(currentGame);
      goList.add(currentGame);
    }

    RadioChoice<String> hostingType =
        new RadioChoice<String>("hosting", new PropertyModel<String>(this, "selected"), goList);

    Form<?> form =
        new Form<Void>("form") {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onSubmit() {
            joinGame(selected);
            info("Selected Type : " + selected);
          }
        };

    add(form);
    form.add(hostingType);
  }

  public void joinGame(String selected) {
    if (true) { //if isPrivate = false --> terminate
      //getSession.setCurrentGame("current Game");
      //TODO: get the current game to navigate to the right GameLobby
      //setResponsePage(getTbialApplication().getGameLobbyPage());
    } /*else {					//else
      passwordField = new PasswordTextField("password", new Model<>(""));
         pwJoinButton =
             new Button("pwjoinbutton") {

               /** UID for serialization. */
    /*private static final long serialVersionUID = 1;

            public void onSubmit() {
              String password = passwordField.getModelObject();
              //TODO: performLogin for Game
            }
          };

      Form<?> form = new Form<>("login");
      form.add(passwordField).add(pwJoinButton);

      add(form);
    }*/
  }
}

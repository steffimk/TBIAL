package de.lmu.ifi.sosy.tbial;

import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.User;

public class PlayersPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public PlayersPage() {

    Form MenuForm =
        new Form("MenuForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            //info("Create New Game");
            setResponsePage(getTbialApplication().getHomePage());
          }
        };

    //Button to direct to List of Games
    Button showGamesButton =
        new Button("showGamesButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getGamesPage());
          }
        };

    MenuForm.add(showGamesButton);

    //Button to direct to list of online players
    Button showPlayersButton =
        new Button("showPlayersButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            info("Already in Player View!");
          }
        };

    MenuForm.add(showPlayersButton);
    add(MenuForm);

    IModel<List<User>> playerModel =
        (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();

    ListView<User> playerList =
        new PropertyListView<>("loggedInUsers", playerModel) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<User> listItem) {
            listItem.add(new Label("name"));
          }
        };

    WebMarkupContainer playerListContainer = new WebMarkupContainer("playerlistContainer");
    playerListContainer.add(playerList);
    playerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    playerListContainer.setOutputMarkupId(true);

    add(playerListContainer);
  }
}

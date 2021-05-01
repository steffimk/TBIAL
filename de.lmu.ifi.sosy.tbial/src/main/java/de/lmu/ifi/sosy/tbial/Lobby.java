package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.User;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

/**
 * Basic lobby page. It <b>should</b> show the list of currently available games. Needs to be
 * extended.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
@AuthenticationRequired
public class Lobby extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private final Button newGameButton;
  private final TextField<String> newGameNameField;
  private final NumberTextField<Integer> maxPlayersField;
  private final AjaxCheckBox isPrivate;
  private final PasswordTextField newGamePwField;

  public Lobby() {
    IModel<List<User>> playerModel = (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();

    ListView<User> playerList = new PropertyListView<>("loggedInUsers", playerModel) {
 
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(final ListItem<User> listItem) {
        listItem.add(new Label("name"));
      }
    };

    newGameButton =
        new Button("newGameButton") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1;

          @Override
          public void onSubmit() {
            String gameName = newGameNameField.getModelObject();
            int maxPlayers = maxPlayersField.getModelObject();
            createNewGame(gameName, maxPlayers, "pw"); // TODO SK
          }
        };
    newGameNameField = new TextField<String>("newGameName", new Model<>("My New Game"));
    newGameNameField.setRequired(true);
    maxPlayersField = new NumberTextField<Integer>("maxPlayers", new Model<>(4));
    maxPlayersField.setRequired(true);
    maxPlayersField.setMinimum(4);
    maxPlayersField.setMaximum(7);
    newGamePwField = new PasswordTextField("newGamePw");
    isPrivate =
        new AjaxCheckBox("isPrivate", new Model<Boolean>(true)) {
          /** UID for serialization. */
          private static final long serialVersionUID = 2;

          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            newGamePwField.setVisible(isPrivate.getModelObject()); // TODO SK
          }
        };

    WebMarkupContainer playerListContainer = new WebMarkupContainer("playerlistContainer");
    playerListContainer.add(playerList);
    playerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    playerListContainer.setOutputMarkupId(true);

    add(playerListContainer);

    Form<?> newGameForm = new Form<>("newGameForm");
    newGameForm
        .add(newGameNameField)
        .add(maxPlayersField)
        .add(isPrivate)
        .add(newGamePwField)
        .add(newGameButton);
    add(newGameForm);
  }

  private void createNewGame(String name, int maxPlayers, String password) {
    // TODO SK
    System.out.printf(
        "Create new game called %s and an max count of %d players\\", name, maxPlayers);
  }
}

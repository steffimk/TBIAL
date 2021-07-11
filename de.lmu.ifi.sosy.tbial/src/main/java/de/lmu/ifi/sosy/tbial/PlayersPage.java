package de.lmu.ifi.sosy.tbial;

import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.ComponentTag;
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
import de.lmu.ifi.sosy.tbial.game.Game;

/** The page that displays all users that are currently online. */
@AuthenticationRequired
public class PlayersPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public PlayersPage() {

    Form<?> menuForm = new Form<>("menuForm");

    String userName = getSession().getUser().getName();

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

    IModel<List<User>> playerModel =
        (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();

    ListView<User> playerList =
        new PropertyListView<>("loggedInUsers", playerModel) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<User> listItem) {
            listItem.add(new Label("name"));
            Form<?> invitationForm = new Form<>("invitationForm");
            Label tooltip =
                new Label("tooltip", "This player already joined your game.") {

                  /** */
                  private static final long serialVersionUID = 1L;

                  @Override
                  public boolean isVisible() {
                    Game currentGame = getGameManager().getGameOfUser(userName);
                    if (currentGame != null
                        && !(currentGame
                            .getPlayers()
                            .containsKey(listItem.getModelObject().getName()))) {
                      return false;
                    }
                    return true;
                  }
                };
            Button inviteButton =
                new Button("inviteButton") {
                  private static final long serialVersionUID = 1L;
                  private String customCSS = null;

                  @Override
                  public void onSubmit() {
                    synchronized (listItem.getModelObject().getInvitations()) {
                      listItem
                          .getModelObject()
                          .invite(
                              new Invitation(
                                  userName,
                                  "has invited you to join a game.",
                                  getGameManager().getGameOfUser(userName).getName()));
                    }
                  }

                  @Override
                  public boolean isVisible() {
                    Game currentGame = getGameManager().getGameOfUser(userName);
                    if (currentGame != null && currentGame.getHost().equals(userName)) {
                      return !listItem
                          .getModelObject()
                          .equals(((TBIALSession) getSession()).getUser());
                    }
                    return false;
                  }

                  @Override
                  public boolean isEnabled() {
                    Game currentGame = getGameManager().getGameOfUser(userName);
                    if (currentGame != null) {
                      return !(currentGame
                          .getPlayers()
                          .containsKey(listItem.getModelObject().getName()));
                    }
                    return true;
                  }

                  @Override
                  protected void onComponentTag(ComponentTag tag) {
                    if (isEnabled()) {
                      customCSS = "buttonStyle";

                    } else {
                      customCSS = null;
                    }
                    super.onComponentTag(tag);
                    tag.put("class", customCSS);
                  }
                };
            invitationForm.add(inviteButton);
            invitationForm.add(tooltip);
            listItem.add(invitationForm);
          }
        };

    WebMarkupContainer playerListContainer = new WebMarkupContainer("playerlistContainer");
    playerListContainer.add(playerList);
    playerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    playerListContainer.setOutputMarkupId(true);

    Form<?> returnToGameLobbyForm = new Form<>("returnToGameLobbyForm");
    Button returnToGameLobbyButton =
        new Button("returnToGameLobbyButton") {

          private static final long serialVersionUID = 1L;
          private String customCSS = null;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getGameLobbyPage());
          }

          @Override
          public boolean isEnabled() {
            Game currentGame = getGameManager().getGameOfUser(userName);
            if (currentGame != null) {
              return true;
            }
            return false;
          }

          @Override
          protected void onComponentTag(ComponentTag tag) {
            if (isEnabled()) {
              customCSS = "buttonStyle";

            } else {
              customCSS = null;
            }
            super.onComponentTag(tag);
            tag.put("class", customCSS);
          }
        };

    Label gameLobbyTooltip =
        new Label("gameLobbyTooltip", "No Active game!") {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isVisible() {
            Game currentGame = getGameManager().getGameOfUser(userName);
            if (currentGame != null) {
              return false;
            }
            return true;
          }
        };
    returnToGameLobbyForm.add(returnToGameLobbyButton);
    returnToGameLobbyForm.add(gameLobbyTooltip);
    add(returnToGameLobbyForm);
    add(playerListContainer);
  }
}

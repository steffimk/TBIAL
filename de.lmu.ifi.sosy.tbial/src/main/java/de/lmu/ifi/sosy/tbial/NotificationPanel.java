package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.GameManager;
import de.lmu.ifi.sosy.tbial.game.Game;

/** Panel showing notification messages */
public class NotificationPanel extends Panel {

  private static final long serialVersionUID = 1L;

  protected GameManager getGameManager() {
    return getTbialApplication().getGameManager();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) super.getApplication();
  }

  public NotificationPanel(String id, User user) {
    super(id);
    

    IModel<List<Invitation>> invitationsModel =
        (IModel<List<Invitation>>) () -> new ArrayList<Invitation>(user.getInvitations());
    ListView<Invitation> invitations =
        new PropertyListView<>("notificationContainer", invitationsModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<Invitation> item) {
            WebMarkupContainer notificationForm = new WebMarkupContainer("notificationForm");
            final Invitation invitation = item.getModelObject();
            notificationForm.add(new Label("notificationSender", invitation.getSender()));
            notificationForm.add(new Label("notificationMessage", invitation.getTextMessage()));
            Form<?> accept = new Form<>("accept");
            Form<?> reject = new Form<>("reject");
            Label tooltip =
                new Label("tooltip", "You cannot accept as long as you are in a game.") {

                  /** */
                  private static final long serialVersionUID = 1L;

                  @Override
                  public boolean isVisible() {
                    Game currentGame = getGameManager().getGameOfUser(user.getName());
                    if (currentGame != null
                        && currentGame.getPlayers().containsKey(user.getName())) {
                      return true;
                    }
                    return false;
                  }
                };
            Label tooltipFull =
                new Label("tooltipFull", "The game is already full.") {

                  /** */
                  private static final long serialVersionUID = 1L;

                  @Override
                  public boolean isVisible() {
                    Game invitedGame =
                        getGameManager().getCurrentGames().get(item.getModelObject().getGameName());
                    if (invitedGame != null
                        && (invitedGame.getPlayers().size() == (invitedGame.getMaxPlayers()))) {
                      return true;
                    }
                    return false;
                  }
                };
            Button acceptButton =
                new Button("acceptButton") {
                  private static final long serialVersionUID = 1L;
                  private String customCSS = null;

                  @Override
                  public void onSubmit() {
                    List<Game> games = getGameManager().getCurrentGamesAsList();
                    for (Game game : games) {
                      if (game.getHost() == invitation.getSender()) {
                        // delete invitation
                        synchronized (user.getInvitations()) {
                          user.getInvitations().remove(invitation);
                        }
                        remove(notificationForm);
                        // add player to game
                        if (getGameManager().joinGame(user.getName(), game, game.getHash())) {

                          // send message in game lobby that invitation was accepted
                          game.getChatMessages()
                              .add(
                                  new ChatMessage(
                                      user.getName() + " accepted the game invitation."));
                          // redirect to game lobby
                          setResponsePage(getTbialApplication().getGameLobbyPage());
                        }
                      }
                    }
                  }

                  @Override
                  public boolean isEnabled() {
                    Game currentGame = getGameManager().getGameOfUser(user.getName());
                    if (currentGame != null) {
                      if (currentGame.getPlayers().containsKey(user.getName())
                          || (currentGame.getPlayers().size() == currentGame.getMaxPlayers())) {
                        return false;
                      }
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
            Button rejectButton =
                new Button("rejectButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {

                    List<Game> games = getGameManager().getCurrentGamesAsList();
                    for (Game game : games) {
                      if (game.getHost() == invitation.getSender()) {
                        // delete invitation
                        synchronized (user.getInvitations()) {
                          user.getInvitations().remove(invitation);
                        }
                        remove(notificationForm);
                        // send message in game lobby that invitation was rejected
                        game.getChatMessages()
                            .add(
                                new ChatMessage(user.getName() + " rejected the game invitation."));

                        setResponsePage(getTbialApplication().getHomePage());
                      }
                    }
                  }
                };
            accept.add(tooltip);
            accept.add(tooltipFull);
            accept.add(acceptButton);
            reject.add(rejectButton);
            notificationForm.add(accept);
            notificationForm.add(reject);
            item.add(notificationForm);
          }
        };
    add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(invitations);
  }
}

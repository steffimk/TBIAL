package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
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
            Form<?> notificationForm = new Form<>("notificationForm");
            final Invitation invitation = item.getModelObject();
            notificationForm.add(new Label("notificationSender", invitation.getSender()));
            notificationForm.add(new Label("notificationMessage", invitation.getTextMessage()));
            Button accept =
                new Button("acceptButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    List<Game> games = getGameManager().getCurrentGamesAsList();
                    for (Game game : games) {
                      if (game.getHost() == invitation.getSender()) {
                        // add player to game
                        ((TBIALSession) getSession()).joinGame(game, game.getHash());
                        // delete invitation
                        user.getInvitations().remove(invitation);
                        remove(notificationForm);
                        // send message in game lobby that invitation was accepted
                        game.getChatMessages()
                            .add(
                                new ChatMessage(
                                    "Update", user.getName() + " accepted the game invitation."));
                        // redirect to game lobby
                        setResponsePage(getTbialApplication().getGameLobbyPage());
                      }
                    }
                  }
                };
            Button reject =
                new Button("rejectButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    // delete invitation
                    user.getInvitations().remove(invitation);
                    remove(notificationForm);
                    // send message in game lobby that invitation was rejected
                    List<Game> games = getGameManager().getCurrentGamesAsList();
                    for (Game game : games) {
                      if (game.getHost() == invitation.getSender()) {
                        game.getChatMessages()
                            .add(
                                new ChatMessage(
                                    "Update", user.getName() + " rejected the game invitation."));
                        setResponsePage(getTbialApplication().getHomePage());
                      }
                    }
                  }
                };
            notificationForm.add(accept);
            notificationForm.add(reject);
            item.add(notificationForm);
          }
        };
    add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(invitations);
  }
}

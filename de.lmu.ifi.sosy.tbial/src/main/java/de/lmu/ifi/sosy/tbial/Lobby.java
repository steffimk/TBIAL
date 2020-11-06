package de.lmu.ifi.sosy.tbial;


import de.lmu.ifi.sosy.tbial.db.User;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import java.util.List;

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

  public Lobby() {
    IModel<List<User>> playerModel = (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();

    ListView<User> playerList = new PropertyListView<>("loggedInUsers", playerModel) {
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

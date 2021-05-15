package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.form.Form;

@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;

  public GameLobby() {
    Form LeaveForm =
        new Form("LeaveForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            //setCurrentGame = null;
            setResponsePage(getTbialApplication().getHomePage());
          }
        };

    add(LeaveForm);
  }
}

package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

/**
 * Basic login form. Redirects to the TBIAL homepage on success, or to the original destination, if
 * one existed.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class Login extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private final Button loginButton;

  private final TextField<String> nameField;

  private final PasswordTextField passwordField;

  public Login() {
    add(new FeedbackPanel("feedback"));

    nameField = new TextField<>("name", new Model<>(""));
    nameField.setRequired(true);
    passwordField = new PasswordTextField("password", new Model<>(""));
    loginButton =
        new Button("loginbutton") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1;

          public void onSubmit() {
            String name = nameField.getModelObject();
            String password = passwordField.getModelObject();
            performLogin(name, password);
          }
        };

    Form<?> form = new Form<>("login");
    form.add(nameField).add(passwordField).add(loginButton);

    add(form);
  }

  private void performLogin(String name, String password) {
    if (getSession().signIn(name, password)) {
      if (getGameManager().getGameOfUser(name) != null) {
        continueToOriginalDestination();
      }
      setResponsePage(getApplication().getHomePage());
    } else {
      error("Wrong login or password. Please try again.");
    }
  }
}

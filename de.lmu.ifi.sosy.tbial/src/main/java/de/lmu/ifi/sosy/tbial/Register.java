package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

/**
 * User registration page. Allows registration of new users with username and password. During
 * registration, the page verifies that the entered user name is not in use yet. This is checked on
 * submit and also when editing the user name field.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class Register extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(Register.class);

  private TextField<String> nameField;

  private PasswordTextField passwordField;

  private PasswordTextField passwordConfirmationField;

  private FeedbackPanel messagePanel;

  private Button registerButton;

  private Label messageLabel;

  public Register() {
    messagePanel = new FeedbackPanel("feedback");
    add(messagePanel);

    nameField = new TextField<String>("name", new Model<>(""));
    nameField.setRequired(true);
    messageLabel = new Label("nameFeedback", new Model<>(" "));
    messageLabel.setOutputMarkupId(true);

    passwordField = new PasswordTextField("password", new Model<>(""));
    passwordConfirmationField = new PasswordTextField("passwordConfirm", new Model<>(""));
    registerButton =
        new Button("register") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onSubmit() {
            String name = nameField.getModelObject();
            String password = passwordField.getModelObject();
            String confirm = passwordConfirmationField.getModelObject();
            performRegistration(name, password, confirm);
          }
        };

    Form<?> form = new Form<>("register");
    form.add(nameField)
        .add(messageLabel)
        .add(passwordField)
        .add(registerButton)
        .add(passwordConfirmationField);

    OnChangeAjaxBehavior onNameChange =
        new OnChangeAjaxBehavior() {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            String name = nameField.getModelObject();
            if (getDatabase().userNameTaken(name)) {
              messageLabel.setDefaultModelObject("Name already taken.");
            } else {
              messageLabel.setDefaultModelObject(" ");
            }
            target.add(messageLabel);
          }
        };

    nameField.add(onNameChange);

    add(form);
  }

  private void performRegistration(String name, String password, String confirm) {
    if (!password.equals(confirm)) {
      error("Password and confirmation do not match. Please verify and try again.");
      LOGGER.debug(
          "User registration for '" + name + "' failed: confirmation and password do not match");
      return;
    }

    User user = getDatabase().register(name, password);
    if (user != null) {
      getSession().setSignedIn(user);
      setResponsePage(getApplication().getHomePage());
      info("Registration successful! You are now logged in.");
      LOGGER.info("New user '" + name + "' registration successful (and logged in)");
    } else {
      error("A user with that name already exists. Please choose another name.");
      LOGGER.debug("New user '" + name + "' registration failed: user already exists");
    }
  }
}

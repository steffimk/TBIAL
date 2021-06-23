package de.lmu.ifi.sosy.tbial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

import de.lmu.ifi.sosy.tbial.game.Game;

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

  private static final Logger LOGGER = LogManager.getLogger(Lobby.class);

  private final Button newGameButton;
  private final TextField<String> newGameNameField;
  private final NumberTextField<Integer> maxPlayersField;
  private final AjaxCheckBox isPrivateCheckBox;
  private final PasswordTextField newGamePwField;
  private final Label nameFeedbackLabel;

  public Lobby() {

    boolean isInGame = getSession().isInGame();

    Form<?> menuForm = new Form<>("menuForm");

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

    Label newGameTooltip =
        new Label("newGameTooltip", "Leave your current game first.") {

          /** */
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isVisible() {
            if (isInGame) {
              return true;
            }
            return false;
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
            boolean isPrivate = isPrivateCheckBox.getModelObject();
            String password = newGamePwField.getModelObject();
            createNewGame(gameName, maxPlayers, isPrivate, password);
          }

          @Override
          public boolean isEnabled() {
            if (isInGame) {
              return false;
            }
            return true;
          }
        };

    newGameNameField = new TextField<String>("newGameName", new Model<>("My New Game"));
    newGameNameField.setRequired(true);

    nameFeedbackLabel = new Label("nameFeedback", () -> getNameFeedback());
    nameFeedbackLabel.setOutputMarkupId(true);

    OnChangeAjaxBehavior onNameChange =
        new OnChangeAjaxBehavior() {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            String name = newGameNameField.getModelObject();
            if (getGameManager().gameNameTaken(name)) {
              nameFeedbackLabel.setDefaultModelObject("Name already taken.");
            } else {
              nameFeedbackLabel.setDefaultModelObject(" ");
            }
            target.add(nameFeedbackLabel);
          }
        };

    newGameNameField.add(onNameChange);

    maxPlayersField = new NumberTextField<Integer>("maxPlayers", new Model<>(4));
    maxPlayersField.setRequired(true);
    maxPlayersField.setMinimum(4);
    maxPlayersField.setMaximum(7);

    WebMarkupContainer passwordContainer = new WebMarkupContainer("passwordContainer");

    isPrivateCheckBox =
        new AjaxCheckBox("isPrivate", new Model<Boolean>(false)) {
          /** UID for serialization. */
          private static final long serialVersionUID = 2;

          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            // Change visibility
            passwordContainer.setVisible(isPrivateCheckBox.getModelObject());

            target.add(passwordContainer);
          }
        };

    passwordContainer.setOutputMarkupPlaceholderTag(true);
    passwordContainer.setVisible(isPrivateCheckBox.getModelObject());

    newGamePwField = new PasswordTextField("newGamePw", new Model<>(""));

    passwordContainer.add(newGamePwField);

    Form<?> newGameForm = new Form<>("newGameForm");
    newGameForm
        .add(newGameTooltip)
        .add(newGameNameField)
        .add(nameFeedbackLabel)
        .add(maxPlayersField)
        .add(isPrivateCheckBox)
        .add(passwordContainer)
        .add(newGameButton);
    add(newGameForm);
  }

  /**
   * Creates a new game and saves it in the database if all requirements are fulfilled.
   *
   * @param name of the new game (unique)
   * @param maxPlayers of the new game
   * @param isPrivate specifies whether the game is password protected
   * @param password can be null if the game is not private
   */
  private void createNewGame(String name, int maxPlayers, boolean isPrivate, String password) {
    String hostName = getSession().getUser().getName();
    // if game name not taken
    if (!getGameManager().gameNameTaken(name)) {
      Game game = new Game(name, maxPlayers, isPrivate, password, hostName);
      getGameManager().addGame(game);
      setResponsePage(getTbialApplication().getGameLobbyPage());
      info("Game creation successful! You are host of a new game");
      LOGGER.info("New game '" + name + "' game creation successful");
    } else {
      error("The name is already taken or the password is empty.");
      LOGGER.debug("New game '" + name + "' creation failed. Name already taken.");
    }
  }

  private String getNameFeedback() {
    return getGameManager().gameNameTaken(newGameNameField.getModelObject())
        ? "Name already taken."
        : " ";
  }
}

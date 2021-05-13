package de.lmu.ifi.sosy.tbial;

<<<<<<< HEAD
=======
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

import de.lmu.ifi.sosy.tbial.db.User;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.Player;
import de.lmu.ifi.sosy.tbial.db.User;

/**
 * Basic lobby page. It <b>should</b> show the list of currently available
 * games. Needs to be extended.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
@AuthenticationRequired
public class Lobby extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

		newGameButton = new Button("newGameButton") {

			/** UID for serialization. */
			private static final long serialVersionUID = 1;
  private final Button newGameButton;
  private final TextField<String> newGameNameField;
  private final NumberTextField<Integer> maxPlayersField;
  private final AjaxCheckBox isPrivateCheckBox;
  private final PasswordTextField newGamePwField;
  private final Label nameFeedbackLabel;
>>>>>>> refs/heads/master

	public Lobby() {
		IModel<List<User>> playerModel = (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();

		ListView<User> playerList = new PropertyListView<>("loggedInUsers", playerModel) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<User> listItem) {
				listItem.add(new Label("name"));
			}
		};

		IModel<List<Game>> gameModel = (IModel<List<Game>>) () -> getTbialApplication().getOpenGames();

		ListView<Game> gameList = new PropertyListView<>("openGames", gameModel) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Game> listItem) {
				final Game game = listItem.getModelObject();
				listItem.add(new Label("name", game.getName()));
				listItem.add(
						new Label("numberOfPlayers", game.getCurrentNumberOfPlayers() + "/" + game.getMaxPlayers()));
				listItem.add(new Label("access", game.isPrivate()));
			}

		};

		newGameButton = new Button("newGameButton") {

<<<<<<< HEAD
			/** UID for serialization. */
			private static final long serialVersionUID = 1;
=======
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
>>>>>>> refs/heads/master

			@Override
			public void onSubmit() {

<<<<<<< HEAD
				String gameName = newGameNameField.getModelObject();
				int maxPlayers = maxPlayersField.getModelObject();
				boolean isPrivate = isPrivateCheckBox.getModelObject();
				String password = newGamePwField.getModelObject();
				createNewGame(gameName, maxPlayers, isPrivate, password);
=======
    maxPlayersField = new NumberTextField<Integer>("maxPlayers", new Model<>(4));
    maxPlayersField.setRequired(true);
    maxPlayersField.setMinimum(4);
    maxPlayersField.setMaximum(7);

    WebMarkupContainer passwordContainer = new WebMarkupContainer("passwordContainer");
    passwordContainer.setOutputMarkupPlaceholderTag(true);

    newGamePwField = new PasswordTextField("newGamePw", new Model<>(""));

    passwordContainer.add(newGamePwField);

    isPrivateCheckBox =
        new AjaxCheckBox("isPrivate", new Model<Boolean>(true)) {
          /** UID for serialization. */
          private static final long serialVersionUID = 2;
>>>>>>> refs/heads/master

<<<<<<< HEAD
			}
		};
=======
          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            // Change visibility
            passwordContainer.setVisible(isPrivateCheckBox.getModelObject());

            target.add(passwordContainer);
          }
        };
>>>>>>> refs/heads/master

		newGameNameField = new TextField<String>("newGameName", new Model<>("My New Game"));
		newGameNameField.setRequired(true);

		nameFeedbackLabel = new Label("nameFeedback", new Model<>(""));
		nameFeedbackLabel.setOutputMarkupId(true);

<<<<<<< HEAD
		OnChangeAjaxBehavior onNameChange = new OnChangeAjaxBehavior() {
=======
    Form<?> newGameForm = new Form<>("newGameForm");
    newGameForm
        .add(newGameNameField)
        .add(nameFeedbackLabel)
        .add(maxPlayersField)
        .add(isPrivateCheckBox)
        .add(passwordContainer)
        .add(newGameButton);
    add(newGameForm);
  }
>>>>>>> refs/heads/master

<<<<<<< HEAD
			/** UID for serialization. */
			private static final long serialVersionUID = 1L;
=======
  /**
   * Creates a new game and saves it in the database if all requirements are fulfilled.
   *
   * @param name of the new game (unique)
   * @param maxPlayers of the new game
   * @param isPrivate specifies whether the game is password protected
   * @param password can be null if the game is not private
   */
  private void createNewGame(String name, int maxPlayers, boolean isPrivate, String password) {
>>>>>>> refs/heads/master

<<<<<<< HEAD
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String name = newGameNameField.getModelObject();
				if (getDatabase().gameNameTaken(name)) {
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
		newGamePwLabel = new Label("newGamePwLabel", new Model<>("Game password"));
		newGamePwLabel.setOutputMarkupPlaceholderTag(true);
		newGamePwField = new PasswordTextField("newGamePw", new Model<>(""));
		newGamePwField.setOutputMarkupPlaceholderTag(true);
		isPrivateCheckBox = new AjaxCheckBox("isPrivate", new Model<Boolean>(true)) {
			/** UID for serialization. */
			private static final long serialVersionUID = 2;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				newGamePwField.setVisible(isPrivateCheckBox.getModelObject());
				newGamePwLabel.setVisible(isPrivateCheckBox.getModelObject());
				target.add(newGamePwField);
				target.add(newGamePwLabel);
			}
		};
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

		WebMarkupContainer playerListContainer = new WebMarkupContainer("playerlistContainer");
		playerListContainer.add(playerList);
		playerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
		playerListContainer.setOutputMarkupId(true);

		add(playerListContainer);
    maxPlayersField = new NumberTextField<Integer>("maxPlayers", new Model<>(4));
    maxPlayersField.setRequired(true);
    maxPlayersField.setMinimum(4);
    maxPlayersField.setMaximum(7);

    WebMarkupContainer passwordContainer = new WebMarkupContainer("passwordContainer");
    passwordContainer.setOutputMarkupPlaceholderTag(true);

    newGamePwField = new PasswordTextField("newGamePw", new Model<>(""));

    passwordContainer.add(newGamePwField);

    isPrivateCheckBox =
        new AjaxCheckBox("isPrivate", new Model<Boolean>(true)) {
          /** UID for serialization. */
          private static final long serialVersionUID = 2;

		WebMarkupContainer gameListContainer = new WebMarkupContainer("gameListContainer");
		gameListContainer.add(gameList);
		gameListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)));
		gameListContainer.setOutputMarkupId(true);
          @Override
          protected void onUpdate(AjaxRequestTarget target) {
            // Change visibility
            passwordContainer.setVisible(isPrivateCheckBox.getModelObject());

            target.add(passwordContainer);
          }
        };

		add(gameListContainer);

		Form<?> newGameForm = new Form<>("newGameForm");
		newGameForm.add(newGameNameField).add(nameFeedbackLabel).add(maxPlayersField).add(isPrivateCheckBox)
				.add(newGamePwLabel).add(newGamePwField).add(newGameButton);
		add(newGameForm);
	}

	/**
	 * Creates a new game and saves it in the database if all requirements are
	 * fulfilled.
	 *
	 * @param name       of the new game (unique)
	 * @param maxPlayers of the new game
	 * @param isPrivate  specifies whether the game is password protected
	 * @param password   can be null if the game is not private
	 */
	private void createNewGame(String name, int maxPlayers, boolean isPrivate, String password) {
		System.out.printf("Trying to create new game called %s and a max count of %d players \n", name, maxPlayers);
    Form<?> newGameForm = new Form<>("newGameForm");
    newGameForm
        .add(newGameNameField)
        .add(nameFeedbackLabel)
        .add(maxPlayersField)
        .add(isPrivateCheckBox)
        .add(passwordContainer)
        .add(newGameButton);
    add(newGameForm);
  }

		Game game = getDatabase().newGame(name, maxPlayers, isPrivate, password);
		if (game != null) {
			getSession().setCurrentGame(game);
			getTbialApplication().gameCreated(game);
			int userId = getSession().getUser().getId();
			Player player = getDatabase().createPlayer(userId, game.getId(), true);
			getSession().setCurrentPlayer(player);
			// setResponsePage(((TBIALApplication) getApplication()).getGameLobbyPage());
			info("Game creation successful! You are host of a new game");
			LOGGER.info("New game '" + name + "' game creation successful");
			System.out.printf("Successfully created new game called %s and a max count of %d players \n", name,
					maxPlayers);
		} else {
			error("There was an error trying to create the game. Please try again."); // TODO SK: Make more
			// specific
			LOGGER.debug("New game '" + name + "' creation failed."); // TODO SK: make more specific, probably name
																		// already taken
			System.out.println("Could not create new game " + name);
		}
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
      getSession().setCurrentGame(game);
      setResponsePage(getTbialApplication().getGameLobbyPage());
      info("Game creation successful! You are host of a new game");
      LOGGER.info("New game '" + name + "' game creation successful");
    } else {
      error("The name is already taken or the password is empty.");
      LOGGER.debug("New game '" + name + "' creation failed. Name already taken.");
    }
  }
  
>>>>>>> refs/heads/master
}

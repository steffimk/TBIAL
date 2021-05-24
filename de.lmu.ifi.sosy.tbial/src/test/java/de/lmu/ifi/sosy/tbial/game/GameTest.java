package de.lmu.ifi.sosy.tbial.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;


public class GameTest {

  private String name;

  private int maxPlayers;

  private String host;

  private boolean isPrivate;

  private String password;

  private boolean hasStarted;

  private Game game;

  @Before
  public void init() {
    password = "savePassword";
    name = "gameName";
    maxPlayers = 4;
    isPrivate = false;
    host = "hostName";
    hasStarted = true;

    game = new Game(name, 7, true, password, "userName");
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNameGiven_throwsException() {
    new Game(null, maxPlayers, isPrivate, password, host);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullHostGiven_throwsException() {
    new Game(name, maxPlayers, isPrivate, password, null);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenPrivateAndNullPasswordGiven_throwsException() {
    new Game(name, maxPlayers, true, null, host);
  }

  @Test
  public void constructor_whenNotPrivateAndNullPasswordGiven_noException() {
    Game game = new Game(name, maxPlayers, false, null, host);
    assertThat(game, is(notNullValue(Game.class)));
  }

  @Test
  public void addNewPlayer_appendsPlayersByNewPlayer() {
    String newPlayer = "newPlayer";
    game.addNewPlayer(newPlayer);
    assertThat(game.getPlayers().containsKey(newPlayer), is(true));
  }

  @Test(expected = NullPointerException.class)
  public void addNewPlayer_whenNullUserNameGiven_throwsException() {
    game.addNewPlayer(null);
  }

  @Test
  public void getName_returnsName() {
    assertThat(game.getName(), is(name));
  }

  @Test
  public void hasStarted_returnsHasStarted() {
    game.setHasStarted(hasStarted);
    assertThat(game.hasStarted(), is(hasStarted));
  }

  @Test
  public void getHashedPassword_returnsHashedPassword() {
    String hash = game.getHash();
    byte[] salt = game.getSalt();

    String calculatedHash = Game.getHashedPassword(password, salt);
    assertThat(calculatedHash, is(hash));
  }

  @Test(expected = NullPointerException.class)
  public void setHost_whenNullHostGiven_throwsException() {
    game.setHost(null);
  }

  @Test
  public void startGame_setsHasStartedToTrue() {
    game.startGame();
    assertThat(game.hasStarted(), is(true));
  }

  @Test
  public void startGame_initializesStack() {
    game.startGame();
    assertThat(game.getStack(), is(notNullValue(Stack.class)));
  }

  @Test
  public void startGame_returnsIfAlreadyStarted() {
    Stack prev = game.getStack();
    game.setHasStarted(true);
    game.startGame();
    assertThat(game.getStack(), is(prev));
  }

  @Test
  public void isAllowedToStartGame_returnsFalseIfNotHost() {
    boolean returnValue = game.isAllowedToStartGame("notHost");
    assertThat(returnValue, is(false));
  }

  @Test
  public void isAllowedToStartGame_returnsFalseIfUsernameNull() {
    boolean returnValue = game.isAllowedToStartGame(null);
    assertThat(returnValue, is(false));
  }

  @Test
  public void isAllowedToStartGame_returnsFalseIfLessThanFourPlayers() {
    Game game = new Game(name, maxPlayers, isPrivate, password, "username");
    boolean returnValue = game.isAllowedToStartGame("username");
    assertThat(returnValue, is(false));
  }

  @Test
  public void isAllowedToStartGame_returnsFalseIfGameHasStarted() {
    Game game = new Game(name, maxPlayers, false, password, "username");
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    game.setHasStarted(true);
    boolean returnValue = game.isAllowedToStartGame("username");
    assertThat(returnValue, is(false));
  }

  @Test
  public void isAllowedToStartGame_returnsTrueIfConditionsFulfilled() {
    Game game = new Game(name, maxPlayers, false, password, "username");
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    boolean returnValue = game.isAllowedToStartGame("username");
    assertThat(returnValue, is(true));
  }
}

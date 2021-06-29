package de.lmu.ifi.sosy.tbial.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;

/** Tests referring to the Game class. */
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
    assertThat(game.getStackAndHeap(), is(notNullValue(StackAndHeap.class)));
  }

  @Test
  public void startGame_returnsIfAlreadyStarted() {
    StackAndHeap prev = game.getStackAndHeap();
    game.setHasStarted(true);
    game.startGame();
    assertThat(game.getStackAndHeap(), is(prev));
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

  @Test
  public void discardHandCard_returnsFalseIfCardNotInHandCards() {
    Game game = getNewGameThatHasStarted();
    StackCard testCard = new AbilityCard(Ability.GOOGLE);
    assertThat(game.discardHandCard(game.getPlayers().get("A"), testCard, true), is(false));
  }

  @Test
  public void discardHandCard_addsCardToHeap() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getPlayers().get("A");
    ArrayList<StackCard> handCards = new ArrayList<StackCard>(player.getHandCards());
    StackCard testCard = handCards.get(0);
    game.discardHandCard(player, testCard, true);
    assertThat(game.getStackAndHeap().getHeap().contains(testCard), is(true));
  }

  @Test
  public void discardHandCard_removesCardFromPlayersHandCards() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getPlayers().get("A");
    ArrayList<StackCard> handCards = new ArrayList<StackCard>(player.getHandCards());
    StackCard testCard = handCards.get(0);
    game.discardHandCard(player, testCard, true);
    assertThat(player.getHandCards().contains(testCard), is(false));
  }
  
  @Test
  public void putCardToPlayer_returnsFalseIfCardNotInHandCards() {
    Game game = getNewGameThatHasStarted();
    StackCard testCard = new AbilityCard(Ability.GOOGLE);
    assertThat(
        game.putCardToPlayer(testCard, game.getPlayers().get("A"), game.getPlayers().get("B")),
        is(false));
  }

  @Test
  public void putCardToPlayer_returnsFalseIfNotTurnOfPlayer() {
    Game game = getNewGameThatHasStarted();
    StackCard testCard = new AbilityCard(Ability.GOOGLE);
    assertThat(
        game.putCardToPlayer(testCard, game.getPlayers().get("B"), game.getPlayers().get("A")),
        is(false));
  }

  @Test
  public void putCardToPlayer_addsCardToReceivedCardsOfReceivingPlayer() {
    Game game = getNewGameThatHasStarted();

    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }

    ArrayList<StackCard> handCards = new ArrayList<StackCard>(player.getHandCards());
    StackCard testCard = handCards.get(0);
    game.putCardToPlayer(testCard, player, receivingPlayer);
    assertThat(receivingPlayer.getReceivedCards().contains(testCard), is(true));
  }

  @Test
  public void putCardToPlayer_removesCardFromPlayersHandCards() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    ArrayList<StackCard> handCards = new ArrayList<StackCard>(player.getHandCards());
    StackCard testCard = handCards.get(0);
    game.putCardToPlayer(testCard, player, receivingPlayer);
    assertThat(player.getHandCards().contains(testCard), is(false));
  }

  @Test
  public void endGame_setsHasEndedToTrue() {
    Game game = getNewGameThatHasStarted();
    game.endGame();
    assertThat(game.hasEnded(), is(true));
  }

  @Test
  public void consultantWins() {
    Game game = getNewGameThatHasStarted();
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(1), game.getEvilCodeMonkeys().get(1).getHandCards());

    game.firePlayer(game.getManager(), game.getManager().getHandCards());
    assertEquals(game.getWinners(), game.getConsultant().getUserName() + " has won.");
  }

  @Test
  public void consultantWinsWith5Players() {
    Game game = new Game(name, 5, false, password, "username");
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    game.addNewPlayer("D");
    game.startGame();
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(1), game.getEvilCodeMonkeys().get(1).getHandCards());
    game.firePlayer(game.getHonestDeveloper(), game.getHonestDeveloper().getHandCards());
    game.firePlayer(game.getManager(), game.getManager().getHandCards());
    assertEquals(game.getWinners(), game.getConsultant().getUserName() + " has won.");
  }

  @Test
  public void managerWins() {
    Game game = getNewGameThatHasStarted();
    game.firePlayer(game.getConsultant(), game.getConsultant().getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(1), game.getEvilCodeMonkeys().get(1).getHandCards());
    assertEquals(game.getWinners(), game.getManager().getUserName() + " has won.");
  }

  @Test
  public void evilCodeMonkeysWin() {
    Game game = getNewGameThatHasStarted();
    game.firePlayer(game.getManager(), game.getManager().getHandCards());
    // wins even though he is fired
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    assertEquals(
        game.getWinners(),
        game.getEvilCodeMonkeys().get(0).getUserName()
            + ", "
            + game.getEvilCodeMonkeys().get(1).getUserName()
            + " have won.");
  }

  @Test
  public void managerAndHonestDevelopersWin() {
    Game game = new Game(name, 5, false, password, "username");
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    game.addNewPlayer("D");
    game.startGame();
    game.firePlayer(game.getConsultant(), game.getConsultant().getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(1), game.getEvilCodeMonkeys().get(1).getHandCards());
    assertEquals(
        game.getWinners(),
        game.getHonestDeveloper().getUserName()
            + " & "
            + game.getManager().getUserName()
            + " have won.");
  }

  // ---------------------- Helper Methods ----------------------

  private Game getNewGameThatHasStarted() {
    Game game = new Game(name, maxPlayers, false, password, "username");
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    game.startGame();
    return game;
  }
}

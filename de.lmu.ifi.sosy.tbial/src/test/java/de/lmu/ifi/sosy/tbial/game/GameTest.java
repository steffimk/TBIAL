package de.lmu.ifi.sosy.tbial.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;

/** Tests referring to the Game class. */
public class GameTest {

  private String name;

  private int maxPlayers;

  private String host;

  private boolean isPrivate;

  private String password;

  private Game game;

  @Before
  public void init() {
    password = "savePassword";
    name = "gameName";
    maxPlayers = 4;
    isPrivate = false;
    host = "hostName";

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
    game.setStartingTimeForTestingOnly(null);
    assertEquals(game.hasStarted(), false);
    game.setStartingTimeForTestingOnly(LocalDateTime.now());
    assertEquals(game.hasStarted(), true);
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
    game.setStartingTimeForTestingOnly(LocalDateTime.now());
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
    game.setStartingTimeForTestingOnly(LocalDateTime.now());
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

    StackCard testCard = new ActionCard(Action.NOT_FOUND);
    player.addToHandCards(testCard);
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
  public void putCardToPlayer_addsMentalHealthIfCardIsSolution() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    receivingPlayer.addToMentalHealth(-1);
    int prevMentalHealth = receivingPlayer.getMentalHealthInt();
    StackCard testCard = new ActionCard(Action.REGEX);
    player.addToHandCards(testCard);
    game.putCardToPlayer(testCard, player, receivingPlayer);
    assertEquals(receivingPlayer.getMentalHealthInt(), prevMentalHealth + 1);
  }

  @Test
  public void playSolution_addsSolutionToHeap() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    StackCard testCard = new ActionCard(Action.REGEX);
    player.addToHandCards(testCard);
    game.putCardToPlayer(testCard, player, player);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard);
  }

  @Test
  public void playLanPartyCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer1;
    Player receivingPlayer2;
    Player receivingPlayer3;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer1 = game.getPlayers().get("B");
      receivingPlayer2 = game.getPlayers().get("C");
      receivingPlayer3 = game.getPlayers().get("username");
    } else if (player == game.getPlayers().get("B")) {
      receivingPlayer1 = game.getPlayers().get("A");
      receivingPlayer2 = game.getPlayers().get("C");
      receivingPlayer3 = game.getPlayers().get("username");
    } else if (player == game.getPlayers().get("C")) {
      receivingPlayer1 = game.getPlayers().get("A");
      receivingPlayer2 = game.getPlayers().get("B");
      receivingPlayer3 = game.getPlayers().get("username");
    } else {
      receivingPlayer1 = game.getPlayers().get("A");
      receivingPlayer2 = game.getPlayers().get("B");
      receivingPlayer3 = game.getPlayers().get("C");
    }

    receivingPlayer1.addToMentalHealth(-1);
    player.addToMentalHealth(-2);
    int prevMentalHealthPlayer = player.getMentalHealthInt();
    int prevMentalHealthReceiver1 = receivingPlayer1.getMentalHealthInt();
    int prevMentalHealthReceiver2 = receivingPlayer2.getMentalHealthInt();
    int prevMentalHealthReceiver3 = receivingPlayer3.getMentalHealthInt();
    StackCard testCard = new ActionCard(Action.LAN);
    player.addToHandCards(testCard);
    game.putCardToPlayer(testCard, player, receivingPlayer1);
    assertEquals(player.getMentalHealthInt(), prevMentalHealthPlayer + 1);
    assertEquals(receivingPlayer1.getMentalHealthInt(), prevMentalHealthReceiver1 + 1);
    assertEquals(receivingPlayer2.getMentalHealthInt(), prevMentalHealthReceiver2);
    assertEquals(receivingPlayer3.getMentalHealthInt(), prevMentalHealthReceiver3);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard);
  }

  @Test
  public void playPersonalCoffeeMachineCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    StackCard testCard = new ActionCard(Action.COFFEE_MACHINE);
    player.addToHandCards(testCard);
    int prevHandCardsReceiving = receivingPlayer.getHandCards().size();
    int prevHandCardsPlayer = player.getHandCards().size();
    // nothing happens; only playable for oneself
    game.putCardToPlayer(testCard, player, receivingPlayer);
    assertEquals(receivingPlayer.getHandCards().size(), prevHandCardsReceiving);
    game.putCardToPlayer(testCard, player, player);
    assertEquals(player.getHandCards().size(), prevHandCardsPlayer + 1);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard);
  }

  @Test
  public void playRedBullDispenserCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    StackCard testCard = new ActionCard(Action.RED_BULL);
    player.addToHandCards(testCard);
    int prevHandCardsReceiving = receivingPlayer.getHandCards().size();
    int prevHandCardsPlayer = player.getHandCards().size();
    // nothing happens; only playable for oneself
    game.putCardToPlayer(testCard, player, receivingPlayer);
    assertEquals(receivingPlayer.getHandCards().size(), prevHandCardsReceiving);
    game.putCardToPlayer(testCard, player, player);
    assertEquals(player.getHandCards().size(), prevHandCardsPlayer + 2);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard);
  }

  @Test
  public void stumblingBlocksMaintenance() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer = game.getTurn().getNextPlayer(game.getTurn().getCurrentPlayerIndex());
    StackCard testCardMaintenance = new StumblingBlockCard(StumblingBlock.MAINTENANCE);
    player.addToHandCards(testCardMaintenance);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);
    game.clickedOnHandCard(player, testCardMaintenance);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);

    assertFalse(receivingPlayer.getReceivedCards().contains(testCardMaintenance));
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        "You can only play a " + testCardMaintenance.toString() + " card for yourself.");

    game.clickedOnAddCardToPlayer(player, player);

    int mentalHealthPrevious = player.getMentalHealthInt();

    // it needs to be the player's turn again
    game.getTurn().switchToNextPlayer();
    game.getTurn().switchToNextPlayer();
    game.getTurn().switchToNextPlayer();
    game.getTurn().getCurrentPlayer().removeAllHandCards(player.getHandCards());
    game.getTurn().setStage(TurnStage.DISCARDING_CARDS);
    game.clickedOnEndTurnButton(game.getTurn().getCurrentPlayer());

    if (game.getStackAndHeap().getUppermostCardOfHeap() == testCardMaintenance) {
      assertEquals(
          game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
          player.getUserName() + " has to do Fortran Maintenance and lost 3 Mental Health Points.");
      assertEquals(player.getMentalHealthInt(), mentalHealthPrevious - 3);
      assertEquals(player.getReceivedCards().contains(testCardMaintenance), false);
    } else {
      assertEquals(receivingPlayer.getReceivedCards().contains(testCardMaintenance), true);
      assertEquals(player.getReceivedCards().contains(testCardMaintenance), false);
      assertEquals(
          game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
          player.getUserName()
              + " doesn't have to do Fortran Maintenance and card moves to "
              + receivingPlayer.getUserName()
              + ".");
    }
  }

  @Test
  public void stumblingBlocksTraining() {
    Game game = getNewGameThatHasStarted();
    game.getTurn().setTurnPlayerUseForTestingOnly(game.getPlayers().get("username"));
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer = game.getTurn().getNextPlayer(game.getTurn().getCurrentPlayerIndex());
    StackCard testCardTraining = new StumblingBlockCard(StumblingBlock.TRAINING);
    player.addToHandCards(testCardTraining);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);
    game.clickedOnHandCard(player, testCardTraining);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);

    if (receivingPlayer.getRole() == Role.MANAGER) {
      assertFalse(receivingPlayer.getReceivedCards().contains(testCardTraining));
      assertEquals(
          game.getChatMessages().get(0).getTextMessage(),
          "You can't play a " + testCardTraining.toString() + " card against a Manager.");
    } else {

      int playerIndex = game.getTurn().getCurrentPlayerIndex();
      playerIndex++;
      if (playerIndex == game.getPlayers().size()) {
        playerIndex = 0;
      }
      player.removeAllHandCards(player.getHandCards());
      game.getTurn().setStage(TurnStage.DISCARDING_CARDS);
      game.clickedOnEndTurnButton(player);

      if (game.getStackAndHeap().getUppermostCardOfHeap() == testCardTraining
          && game.getTurn().getCurrentPlayer() == receivingPlayer) {
        assertEquals(
            game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
            receivingPlayer.getUserName() + " doesn't have to do an off the job training.");
        assertEquals(receivingPlayer.getReceivedCards().contains(testCardTraining), false);
      }
      if (game.getStackAndHeap().getUppermostCardOfHeap() == testCardTraining
          && !(game.getTurn().getCurrentPlayer() == receivingPlayer)) {
        assertEquals(
            game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
            receivingPlayer.getUserName()
                + " has to do an off the job training and has to skip his/her turn.");
        assertEquals(receivingPlayer.getReceivedCards().contains(testCardTraining), false);
        assertEquals(
            game.getTurn().getCurrentPlayer() == game.getTurn().getNextPlayer(playerIndex), true);
      }
    }
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
    if (game.getPlayers().get("username").getRole() == Role.CONSULTANT) {
      assertEquals(game.getWinners(game.getPlayers().get("username")), "You have won.");
    } else {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getConsultant().getUserName() + " has won.");
    }
    assertEquals(game.getGroupWon(), "The Consultant has won!");
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
    if (game.getPlayers().get("username").getRole() == Role.CONSULTANT) {
      assertEquals(game.getWinners(game.getPlayers().get("username")), "You have won.");
    } else {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getConsultant().getUserName() + " has won.");
    }
    assertEquals(game.getGroupWon(), "The Consultant has won!");
  }

  @Test
  public void managerWins() {
    Game game = getNewGameThatHasStarted();
    game.firePlayer(game.getConsultant(), game.getConsultant().getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    game.firePlayer(
        game.getEvilCodeMonkeys().get(1), game.getEvilCodeMonkeys().get(1).getHandCards());
    if (game.getPlayers().get("username").getRole() == Role.MANAGER) {
      assertEquals(game.getWinners(game.getPlayers().get("username")), "You have won.");
    } else {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getManager().getUserName() + " has won.");
    }
    assertEquals(game.getGroupWon(), "The Manager has won!");
  }

  @Test
  public void evilCodeMonkeysWin() {
    Game game = getNewGameThatHasStarted();
    game.firePlayer(game.getManager(), game.getManager().getHandCards());
    // wins even though he is fired
    game.firePlayer(
        game.getEvilCodeMonkeys().get(0), game.getEvilCodeMonkeys().get(0).getHandCards());
    if (game.getEvilCodeMonkeys().get(0).getUserName() == "username") {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          "You, " + game.getEvilCodeMonkeys().get(1).getUserName() + " have won.");
    } else if (game.getEvilCodeMonkeys().get(1).getUserName() == "username") {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getEvilCodeMonkeys().get(0).getUserName() + ", You have won.");
    } else {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getEvilCodeMonkeys().get(0).getUserName()
              + ", "
              + game.getEvilCodeMonkeys().get(1).getUserName()
              + " have won.");
    }
    assertEquals(game.getGroupWon(), "The Evil Code Monkeys have won!");
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
    if (game.getPlayers().get("username").getRole() == Role.HONEST_DEVELOPER) {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          "You & " + game.getManager().getUserName() + " have won.");
    } else if (game.getPlayers().get("username").getRole() == Role.MANAGER) {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getHonestDeveloper().getUserName() + " & you have won.");
    } else {
      assertEquals(
          game.getWinners(game.getPlayers().get("username")),
          game.getHonestDeveloper().getUserName()
              + " & "
              + game.getManager().getUserName()
              + " have won.");
    }
    assertEquals(game.getGroupWon(), "The Manager and the Honest Developer have won!");
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

package de.lmu.ifi.sosy.tbial.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;

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
    game.putCardToPlayer(testCardMaintenance, player, receivingPlayer);
    int mentalHealthPrevious = receivingPlayer.getMentalHealthInt();
    player.removeAllHandCards(player.getHandCards());
    game.getTurn().setStage(TurnStage.DISCARDING_CARDS);
    game.clickedOnEndTurnButton(player);

    if (game.getStackAndHeap().getUppermostCardOfHeap() == testCardMaintenance) {
      assertEquals(
          game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
          receivingPlayer.getUserName()
              + " has to do Fortran Maintenance and lost 3 Mental Health Points.");
      assertEquals(receivingPlayer.getMentalHealthInt(), mentalHealthPrevious - 3);
      assertEquals(receivingPlayer.getReceivedCards().contains(testCardMaintenance), false);
    } else {
      Turn turn = game.getTurn();
      Player nextPlayer = turn.getNextPlayer(turn.getCurrentPlayerIndex());
      assertEquals(nextPlayer.getReceivedCards().contains(testCardMaintenance), true);
      assertEquals(receivingPlayer.getReceivedCards().contains(testCardMaintenance), false);
      assertEquals(
          game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
          receivingPlayer.getUserName()
              + " doesn't have to do Fortran Maintenance and card moves to "
              + nextPlayer.getUserName()
              + ".");
    }
  }

  @Test
  public void stumblingBlocksTraining() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer = game.getTurn().getNextPlayer(game.getTurn().getCurrentPlayerIndex());
    StackCard testCardTraining = new StumblingBlockCard(StumblingBlock.TRAINING);
    player.addToHandCards(testCardTraining);
    game.putCardToPlayer(testCardTraining, player, receivingPlayer);
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

  @Test
  public void testPreviousJobCards() {
    AbilityCard microsoft = new AbilityCard(Ability.MICROSOFT);
    playPreviousJobCard(microsoft, 1, " worked at Microsoft and received a prestige of 1.");

    AbilityCard google = new AbilityCard(Ability.GOOGLE);
    playPreviousJobCard(google, 2, " worked at Google and received a prestige of 2.");

    AbilityCard nasa = new AbilityCard(Ability.NASA);
    playPreviousJobCard(nasa, 3, " worked at Nasa and received a prestige of 3.");
  }

  @Test
  public void playAccentureCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    AbilityCard testCard = new AbilityCard(Ability.ACCENTURE);
    StackCard bugCard1 = new ActionCard(Action.NOT_FOUND);
    StackCard bugCard2 = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testCard);
    player.addToHandCards(bugCard1);
    player.addToHandCards(bugCard2);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // nothing happens; only playable for oneself
    game.clickedOnHandCard(player, testCard);
    game.clickedOnPlayAbility(player, receivingPlayer);
    assertEquals(receivingPlayer.getPlayedAbilityCards().size(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        "You can only play the " + testCard.toString() + " card for yourself.");

    // play one bug card against receivingPlayer
    game.clickedOnHandCard(player, bugCard1);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);
    assertEquals(player.getHandCards().contains(bugCard1), false);
    assertEquals(receivingPlayer.getReceivedCards().size(), 1);
    assertEquals(receivingPlayer.getReceivedCards().contains(bugCard1), true);
    assertEquals(receivingPlayer.getBugBlocks().size(), 1);

    game.getTurn().setStage(TurnStage.CHOOSING_CARD_TO_BLOCK_WITH);
    ActionCard defend = new ActionCard(Action.COFFEE);
    receivingPlayer.addToHandCards(defend);
    game.clickedOnHandCard(receivingPlayer, defend);

    // play second bug card against receivingPlayer -> not possible
    game.clickedOnHandCard(player, bugCard2);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);
    assertEquals(receivingPlayer.getBugBlocks().size(), 0);
    assertEquals(receivingPlayer.getReceivedCards().size(), 0);
    assertEquals(receivingPlayer.getReceivedCards().contains(bugCard2), false);
    assertEquals(player.getHandCards().contains(bugCard2), true);

    assertEquals(game.getChatMessages().get(0).getTextMessage(), "You cannot play another bug.");


    // test Accenture card on self
    game.clickedOnHandCard(player, testCard);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    assertEquals(player.getPrestigeInt(), 0);
    assertEquals(player.getMaxBugCardsPerTurn(), Integer.MAX_VALUE);
    assertEquals(

        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " worked at Accenture and can play as many bugs as he/she wants.");

    // play second bug card against receivingPlayer again -> now it works
    game.clickedOnHandCard(player, bugCard2);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);
    assertEquals(receivingPlayer.getReceivedCards().size(), 1);
    assertEquals(receivingPlayer.getReceivedCards().contains(bugCard2), true);
    assertEquals(player.getHandCards().contains(bugCard2), false);
    assertEquals(receivingPlayer.getBugBlocks().size(), 1);
  }

  @Test
  public void playSeveralPreviousJobs() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    AbilityCard testCard1 = new AbilityCard(Ability.MICROSOFT);
    AbilityCard testCard2 = new AbilityCard(Ability.GOOGLE);
    player.addToHandCards(testCard1);
    player.addToHandCards(testCard2);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // Microsoft test card on self
    game.clickedOnHandCard(player, testCard1);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    assertEquals(player.getPlayedAbilityCards().contains(testCard1), true);
    assertEquals(player.getPrestigeInt(), 1);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " worked at Microsoft and received a prestige of 1.");
    assertEquals(player.getHandCards().contains(testCard1), false);
    assertEquals(player.getHandCards().contains(testCard2), true);

    // Google test card on self
    game.clickedOnHandCard(player, testCard2);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    assertEquals(player.getPlayedAbilityCards().contains(testCard2), true);
    assertEquals(player.getPrestigeInt(), 2);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " worked at Google and received a prestige of 2.");
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard1);
    assertEquals(player.getHandCards().contains(testCard2), false);
  }

  @Test
  public void canOnlyAttackPlayerWithLowerPrestige() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    StackCard bugCard = new ActionCard(Action.NOT_FOUND);
    player.addToHandCards(bugCard);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // player can't attack receivingPlayer because of lower prestige
    receivingPlayer.updatePrestige(1);
    game.clickedOnHandCard(player, bugCard);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName()
            + " can't attack "
            + receivingPlayer.getUserName()
            + " because of lower prestige.");

    // player can attack receivingPlayer because of higher/same prestige
    player.updatePrestige(1);
    game.clickedOnHandCard(player, bugCard);
    game.clickedOnAddCardToPlayer(player, receivingPlayer);
    assertEquals(player.getHandCards().contains(bugCard), false);
    assertEquals(receivingPlayer.getReceivedCards().size(), 1);
    assertEquals(receivingPlayer.getReceivedCards().contains(bugCard), true);
    assertEquals(receivingPlayer.getBugBlocks().size(), 1);
  }

  @Test
  public void playSunglassesCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    AbilityCard testCard = new AbilityCard(Ability.SUNGLASSES);
    player.addToHandCards(testCard);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // nothing happens; only playable for oneself
    game.clickedOnHandCard(player, testCard);
    game.clickedOnPlayAbility(player, receivingPlayer);
    assertEquals(receivingPlayer.getPlayedAbilityCards().size(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        "You can only play the " + testCard.toString() + " card for yourself.");

    // test card on self
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    receivingPlayer.updatePrestige(2);
    assertEquals(player.getPrestigeInt(), 0);
    // sees other player with 1 prestige even though he actually has 2 prestige
    assertEquals(game.calculatePrestige(player, receivingPlayer), 1);
    assertEquals(receivingPlayer.getPrestigeInt(), 2);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on sunglasses and sees everybody with -1 prestige.");
  }

  @Test
  public void playTieCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    AbilityCard testCard = new AbilityCard(Ability.TIE);
    player.addToHandCards(testCard);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // nothing happens; only playable for oneself
    game.clickedOnHandCard(player, testCard);
    game.clickedOnPlayAbility(player, receivingPlayer);
    assertEquals(receivingPlayer.getPlayedAbilityCards().size(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        "You can only play the " + testCard.toString() + " card for yourself.");

    // test card on self
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    // player is seen with 1 prestige even though he actually has 0 prestige
    assertEquals(game.calculatePrestige(receivingPlayer, player), 1);
    assertEquals(player.getPrestigeInt(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on a tie and is seen with +1 prestige by everyone.");
  }

  @Test
  public void playTwoTiesAndTwoSunglassesCards() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    AbilityCard testCard1 = new AbilityCard(Ability.TIE);
    AbilityCard testCard2 = new AbilityCard(Ability.TIE);
    AbilityCard testCard3 = new AbilityCard(Ability.SUNGLASSES);
    AbilityCard testCard4 = new AbilityCard(Ability.SUNGLASSES);
    player.addToHandCards(testCard1);
    player.addToHandCards(testCard2);
    player.addToHandCards(testCard3);
    player.addToHandCards(testCard4);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // Ties
    // test card on self
    game.clickedOnHandCard(player, testCard1);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    // player is seen with 1 prestige even though he actually has 0 prestige
    assertEquals(game.calculatePrestige(receivingPlayer, player), 1);
    assertEquals(player.getPrestigeInt(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on a tie and is seen with +1 prestige by everyone.");
    game.clickedOnHandCard(player, testCard2);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard1);
    // player is seen with 1 prestige even though he actually has 0 prestige
    assertEquals(game.calculatePrestige(receivingPlayer, player), 1);
    assertEquals(player.getPrestigeInt(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on a tie and is seen with +1 prestige by everyone.");

    // Sunglasses
    // test card on self
    game.clickedOnHandCard(player, testCard3);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 2);
    // player is seen with 1 prestige even though he actually has 0 prestige
    assertEquals(game.calculatePrestige(receivingPlayer, player), 1);
    receivingPlayer.updatePrestige(2);
    assertEquals(player.getPrestigeInt(), 0);
    // sees other player with 1 prestige even though he actually has 2 prestige
    assertEquals(game.calculatePrestige(player, receivingPlayer), 1);
    assertEquals(receivingPlayer.getPrestigeInt(), 2);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on sunglasses and sees everybody with -1 prestige.");
    game.clickedOnHandCard(player, testCard4);
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 2);
    assertEquals(game.getStackAndHeap().getUppermostCardOfHeap(), testCard3);
    receivingPlayer.updatePrestige(2);
    assertEquals(player.getPrestigeInt(), 0);
    // sees other player with 1 prestige even though he actually has 2 prestige
    assertEquals(game.calculatePrestige(player, receivingPlayer), 1);
    assertEquals(receivingPlayer.getPrestigeInt(), 2);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + " put on sunglasses and sees everybody with -1 prestige.");
  }

  @Test
  public void playBugCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver = game.getTurn().getNextPlayer(game.getTurn().getCurrentPlayerIndex());

    StackCard testBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testBugCard);

    game.putCardToPlayer(testBugCard, player, receiver);

    assertThat(receiver.getReceivedCards().contains(testBugCard), is(true));
    assertEquals(game.getTurn().getAttackedPlayer(), receiver);
    assertEquals(game.getTurn().getLastPlayedBugCard(), testBugCard);
    assertEquals(game.getTurn().getLastPlayedBugCardBy(), player);
  }

  @Test
  public void canNotDefendBugCard() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }
    receiver.getHandCards().clear();

    int receiverMentalHealth = receiver.getMentalHealthInt();

    StackCard testBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testBugCard);

    game.putCardToPlayer(testBugCard, player, receiver);

    assertThat(receiver.canDefendBug(), is(false));
    assertNotEquals(receiver.getMentalHealth(), receiverMentalHealth);
  }

  @Test
  public void canDefendBugCardWithLameExcuse() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }

    StackCard testBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testBugCard);

    StackCard testLameExcuseCard = new ActionCard(Action.FEATURE);
    receiver.addToHandCards(testLameExcuseCard);

    canDefendWithCard(game, player, receiver, testBugCard, testLameExcuseCard);
  }

  @Test
  public void canDefendBugCardWithSolution() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }

    StackCard testBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testBugCard);

    StackCard testSolutionCard = new ActionCard(Action.COFFEE);
    receiver.addToHandCards(testSolutionCard);

    canDefendWithCard(game, player, receiver, testBugCard, testSolutionCard);
  }

  public void canDefendWithCard(
      Game game,
      Player player,
      Player receiver,
      StackCard testBugCard,
      StackCard cardToDefendWith) {
    int heapSize = game.getStackAndHeap().getHeap().size();
    int receiverMentalHealth = receiver.getMentalHealthInt();

    game.putCardToPlayer(testBugCard, player, receiver);

    assertThat(receiver.canDefendBug(), is(true));
    assertNotEquals(receiver.getMentalHealth(), receiverMentalHealth);

    game.defendBugImmediately(receiver, (ActionCard) cardToDefendWith);

    assertEquals(receiver.getMentalHealthInt(), receiverMentalHealth);
    assertEquals(game.getStackAndHeap().getHeap().size(), heapSize + 2);
    assertThat(game.getStackAndHeap().getHeap().contains(testBugCard), is(true));
    assertThat(game.getStackAndHeap().getHeap().contains(cardToDefendWith), is(true));
    assertThat(player.getHandCards().contains(cardToDefendWith), is(false));
    assertThat(player.getReceivedCards().contains(testBugCard), is(false));
  }

  @Test
  public void drawTwoCardsAndCanPlayOneBugPerTurn() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }

    int handCardSizePlayer = player.getHandCards().size();

    StackCard testNullpointerBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testNullpointerBugCard);
    StackCard testHatesUiBugCard = new ActionCard(Action.HATES_UI);
    player.addToHandCards(testHatesUiBugCard);

    StackCard testSolutionCard = new ActionCard(Action.COFFEE);
    receiver.addToHandCards(testSolutionCard);

    assertEquals(player.getHandCards().size(), handCardSizePlayer + 2);
    game.clickedOnDrawCardsButton(player);
    assertEquals(player.getHandCards().size(), handCardSizePlayer + 4);

    player.setSelectedHandCard(testNullpointerBugCard);
    game.clickedOnAddCardToPlayer(player, receiver);

    assertEquals(player.getHandCards().size(), handCardSizePlayer + 3);
    assertThat(player.getHandCards().contains(testNullpointerBugCard), is(false));
    assertEquals(game.getTurn().getPlayedBugCardsInThisTurn(), 1);

    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    player.setSelectedHandCard(testHatesUiBugCard);
    game.clickedOnAddCardToPlayer(player, receiver);

    assertEquals(
        game.getChatMessages().get(game.getChatMessages().size() - 1).getTextMessage(),
        "You cannot play another bug.");
    assertThat(player.getHandCards().contains(testHatesUiBugCard), is(true));
    assertEquals(game.getTurn().getPlayedBugCardsInThisTurn(), 1);
  }

  @Test
  public void canEndTurn() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();

    int handCardSizeBeforeDraw = player.getHandCards().size();
    game.clickedOnDrawCardsButton(player);

    assertEquals(handCardSizeBeforeDraw + 2, player.getHandCards().size());
    assertThat(player.canEndTurn(), is(false));
    
    game.clickedOnDiscardButton(player);
    assertEquals(game.getTurn().getStage(), TurnStage.DISCARDING_CARDS);
    assertThat(player.canEndTurn(), is(false));
    
    player.setSelectedHandCard(player.getHandCards().stream().findFirst().get());
    game.clickedOnHeap(player);
    assertThat(player.canEndTurn(), is(false));
    
    player.setSelectedHandCard(player.getHandCards().stream().findFirst().get());
    game.clickedOnHeap(player);
    assertThat(player.canEndTurn(), is(true));
  }

  @Test
  public void TurnStageChangesIfBugIsPlayedAgainstPlayer() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }

    StackCard testNullpointerBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testNullpointerBugCard);

    StackCard testSolutionCard = new ActionCard(Action.COFFEE);
    receiver.addToHandCards(testSolutionCard);

    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    player.setSelectedHandCard(testNullpointerBugCard);
    game.clickedOnAddCardToPlayer(player, receiver);

    assertEquals(game.getTurn().getStage(), TurnStage.WAITING_FOR_PLAYER_RESPONSE);
  }

  @Test
  public void ChooseCardToDefend() {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receiver;
    if (player == game.getPlayers().get("A")) {
      receiver = game.getPlayers().get("B");
    } else {
      receiver = game.getPlayers().get("A");
    }

    StackCard testBugCard = new ActionCard(Action.NULLPOINTER);
    player.addToHandCards(testBugCard);

    StackCard testSolutionCardCoffee = new ActionCard(Action.COFFEE);
    receiver.addToHandCards(testSolutionCardCoffee);

    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    player.setSelectedHandCard(testBugCard);
    game.clickedOnAddCardToPlayer(player, receiver);
    assertThat(receiver.getReceivedCards().contains(testBugCard), is(true));
    assertEquals(game.getTurn().getAttackedPlayer(), receiver);
    assertEquals(game.getTurn().getLastPlayedBugCard(), testBugCard);

    game.getTurn().setStage(TurnStage.CHOOSING_CARD_TO_BLOCK_WITH);

    game.clickedOnHandCard(receiver, testSolutionCardCoffee);

    assertEquals(game.getTurn().getAttackedPlayer(), null);
    assertEquals(game.getTurn().getStage(), TurnStage.PLAYING_CARDS);
    assertEquals(receiver.getSelectedHandCard(), null);
    assertThat(receiver.getReceivedCards().contains(testBugCard), is(false));
    assertThat(receiver.getHandCards().contains(testSolutionCardCoffee), is(false));
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

  private void playPreviousJobCard(
      AbilityCard testCard, int expectedPrestige, String expectedSuccessMessage) {
    Game game = getNewGameThatHasStarted();
    Player player = game.getTurn().getCurrentPlayer();
    Player receivingPlayer;
    if (player == game.getPlayers().get("A")) {
      receivingPlayer = game.getPlayers().get("B");
    } else {
      receivingPlayer = game.getPlayers().get("A");
    }
    player.addToHandCards(testCard);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    // nothing happens; only playable for oneself
    game.clickedOnHandCard(player, testCard);
    game.clickedOnPlayAbility(player, receivingPlayer);
    assertEquals(receivingPlayer.getPlayedAbilityCards().size(), 0);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        "You can only play the " + testCard.toString() + " card for yourself.");

    // test card on self
    game.clickedOnPlayAbility(player, player);
    assertEquals(player.getPlayedAbilityCards().size(), 1);
    assertEquals(player.getPrestigeInt(), expectedPrestige);
    assertEquals(
        game.getChatMessages().get(0).getTextMessage(),
        player.getUserName() + expectedSuccessMessage);
  }
}

package de.lmu.ifi.sosy.tbial.game;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.lmu.ifi.sosy.tbial.BugBlock;
import de.lmu.ifi.sosy.tbial.ChatMessage;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.Card.CardType;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;

/** A game. Contains all information about a game. */
public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(Game.class);

  private String name;

  private int maxPlayers;

  private Map<String, Player> players;

  private String host;

  private boolean isPrivate;

  private String hash;
  private byte[] salt;

  private static final DateTimeFormatter timeFormatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
  private LocalDateTime startingTime;
  private LocalDateTime endingTime;

  private StackAndHeap stackAndHeap;

  private Turn turn;

  private LinkedList<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

  private Player manager;
  private Player consultant;
  private List<Player> monkeys = new ArrayList<Player>();
  private Player developer;

  private String winners = "";

  private GameStatistics statistics;

  private boolean hasPlayedBugBeenDefended;

  public Game(String name, int maxPlayers, boolean isPrivate, String password, String userName) {
    this.name = requireNonNull(name);
    this.maxPlayers = requireNonNull(maxPlayers);
    if (maxPlayers > 7) {
      this.maxPlayers = 7;
    } else if (maxPlayers < 4) {
      this.maxPlayers = 4;
    }
    this.setHost(requireNonNull(userName));
    this.players = Collections.synchronizedMap(new HashMap<>());

    addNewPlayer(userName);

    this.isPrivate = requireNonNull(isPrivate);
    if (isPrivate) {
      requireNonNull(password);
      SecureRandom random = new SecureRandom();
      byte[] saltByteArray = new byte[16];
      random.nextBytes(saltByteArray);
      this.salt = saltByteArray;
      this.hash = getHashedPassword(password, saltByteArray);
    }
    this.startingTime = null;
    this.endingTime = null;
    this.statistics = new GameStatistics();
    this.hasPlayedBugBeenDefended = false;
  }

  /**
   * Creates a new instance of Player and adds it to the game's players.
   *
   * @param userName
   */
  public void addNewPlayer(String userName) {
    Player newPlayer = new Player(requireNonNull(userName));
    players.put(userName, newPlayer);
  }

  /**
   * Checks whether the game is ready to start and whether this user is allowed to do so.
   *
   * @param username of the user trying to start the game
   * @return Whether this user is allowed to start the game.
   */
  public boolean isAllowedToStartGame(String username) {
    if (username != host) {
      LOGGER.info("Checking if user is allowed to start game: User is not host.");
      return false;
    } else if (players.size() < 4) {
      LOGGER.info("Checking if user is allowed to start game: Game has less than four players.");
      return false;
    } else if (hasStarted()) {
      LOGGER.info("Checking if user is allowed to start game: Game has already started.");
      return false;
    }
    return true;
  }

  /** Starts the game. */
  public synchronized void startGame() {
    if (hasStarted()) {
      return;
    }
    startingTime = LocalDateTime.now();
    chatMessages.clear();
    distributeRoleCards();
    for (Player player : getInGamePlayersList()) {
      switch (player.getRole()) {
        case MANAGER:
          manager = player;
          break;
        case CONSULTANT:
          consultant = player;
          break;
        case HONEST_DEVELOPER:
          developer = player;
          break;
        case EVIL_CODE_MONKEY:
          monkeys.add(player);
          break;
      }
    }
    distributeCharacterCardsAndInitialMentalHealthPoints();
    stackAndHeap = new StackAndHeap();
    turn = new Turn(new ArrayList<Player>(players.values()));
    turn.determineStartingPlayer();
    distributeInitialHandCards();
  }

  /** Distributing the role cards to the players. */
  private void distributeRoleCards() {
    List<RoleCard> roleCards = RoleCard.getRoleCards(players.size());
    int i = 0;
    for (Player player : players.values()) {
      player.setRoleCard(roleCards.get(i));
      i++;
    }
  }

  /** Distributing the character cards to the players. */
  private void distributeCharacterCardsAndInitialMentalHealthPoints() {
    List<CharacterCard> characterCards = CharacterCard.getCharacterCards(players.size());
    int i = 0;
    for (Player player : players.values()) {
      player.setCharacterCard(characterCards.get(i));
      player.initialMentalHealth();
      i++;
    }
  }

  /**
   * Hands out the initial hand cards to each player. A player receives as many hand cards as he has
   * mental health points.
   */
  private void distributeInitialHandCards() {
    for (Player player : players.values()) {
      Set<StackCard> handCards = new HashSet<>();
      for (int i = 0; i < player.getMentalHealthInt(); i++) {
        handCards.add(stackAndHeap.drawCard());
      }
      player.addToHandCards(handCards);
    }
  }

  /**
   * Discarding a hand card. Removes the card from the player's hand cards and moves it to the heap.
   * Does not check whether the player is allowed to do so.
   *
   * @param player The player who wants to discard the card.
   * @param card The card the player wants to discard.
   * @param isNormalDiscard <code>true</code> if the card is added to the heap in the discard stage
   * @return <code>true</code> if the discarding was successful, <code>false</code> otherwise
   */
  public boolean discardHandCard(Player player, StackCard card, boolean isNormalDiscard) {
    if (player.removeHandCard(card)) {
      stackAndHeap.addToHeap(card, player, isNormalDiscard);
      return true;
    }
    return false;
  }

  public void putCardOnHeap(Player player, StackCard card) {
    stackAndHeap.addToHeap(card, player, false);
  }

  /**
   * Removes the card from the player's hand cards and adds it to the receiver's received cards.
   * Deals with the different types of cards respectively.
   *
   * @param card The card to be played
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   * @return <code>true</code> if the action was successful, <code>false</code> otherwise
   */
  public boolean putCardToPlayer(StackCard card, Player player, Player receiver) {
    if (player.removeHandCard(card)) {
      if (card.isBug()) {
        playBug(card, player, receiver);
        return true;
      }
      if (((Card) card).getCardType() == CardType.ACTION && ((ActionCard) card).isSolution()) {
        playSolution(card, player, receiver);
        return true;
      }

      if (((Card) card).getCardType() == CardType.ACTION && ((ActionCard) card).isSpecial()) {
        if (((ActionCard) card).getAction() == Action.LAN) {
          playLanParty(card, player, receiver);
          return true;
        }

        if (((ActionCard) card).getAction() == Action.COFFEE_MACHINE) {
          playCoffeeMachine(card, player);
          return true;
        } else if (((ActionCard) card).getAction() == Action.RED_BULL) {
          playRedBull(card, player);
          return true;
        }
      }
      receiver.receiveCard(card);
      return false;
    }
    return false;
  }

  /**
   * Call when a player plays a solution card.
   *
   * @param card The card that is played.
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   */
  private void playSolution(StackCard card, Player player, Player receiver) {
    receiver.addToMentalHealth(1);
    stackAndHeap.addToHeap(card, receiver, false);
    String message = "the solution \"" + card.toString() + "\"";
    if (player.equals(receiver)) {
      message = player.getUserName() + " played " + message + ".";
    } else {
      message = receiver.getUserName() + " received " + message + " from " + player.getUserName();
    }
    chatMessages.add(new ChatMessage(message));
  }

  /**
   * Call when a player plays a bug. If the card is a bug and the receiver owns a bug delegation
   * card, there's a 25% chance the card will get blocked and added to the heap immediately.
   *
   * @param card The card that is played.
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   */
  private void playBug(StackCard card, Player player, Player receiver) {
    turn.incrementPlayedBugCardsThisTurn();
    turn.setLastPlayedBugCard((ActionCard) card);
    turn.setLastPlayedBugCardBy(player);

    if (receiver.bugGetsBlockedByBugDelegationCard(chatMessages, receiver)) {
      // Receiver moves card to heap immediately without having to react
      stackAndHeap.addToHeap(card, receiver, false);
      chatMessages.add(
          new ChatMessage(
              receiver.getUserName()
                  + " blocked \""
                  + card.toString()
                  + "\" with a bug delegation card."));
      statistics.bugDelegationCardBlockedBug();
      return;
    }
    receiver.receiveCard(card);
    turn.setAttackedPlayer(receiver);

    LOGGER.info(player.getUserName() + " played bug card " + card.toString());

    receiver.blockBug(new BugBlock(player.getUserName()));
    receiver.addToMentalHealth(-1);
    turn.setStage(Turn.TurnStage.WAITING_FOR_PLAYER_RESPONSE);
  }

  /**
   * Call when a player plays a LAN Party card. Adds 1 mental health point to everyone!
   *
   * @param card The card that is played.
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   */
  private void playLanParty(StackCard card, Player player, Player receiver) {
    for (Player p : getPlayers().values()) {
      p.addToMentalHealth(1);
    }
    stackAndHeap.addToHeap(card, receiver, false);
    String message = player.getUserName() + " played " + card.toString() + ".";
    chatMessages.add(new ChatMessage(message));
  }

  /**
   * Call when a player plays a Personal Coffee Machine card. Player gets 2 new cards from the
   * stack.
   *
   * @param card The card that is played.
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   */
  private void playCoffeeMachine(StackCard card, Player player) {
    drawCardsFromStack(player);
    stackAndHeap.addToHeap(card, player, false);
    String message =
        player.getUserName()
            + " played "
            + card.toString()
            + " and received 2 new cards from the stack.";
    chatMessages.add(new ChatMessage(message));
  }

  /**
   * Call when a player plays a Red Bull Dispenser card. Player gets 3 new cards from the stack.
   *
   * @param card The card that is played.
   * @param player The player who is playing the card.
   * @param receiver The player who is receiving the card.
   */
  private void playRedBull(StackCard card, Player player) {
    drawCardsFromStack(player);
    player.addToHandCards(stackAndHeap.drawCard());
    stackAndHeap.addToHeap(card, player, false);
    String message =
        player.getUserName()
            + " played "
            + card.toString()
            + " and received 3 new cards from the stack.";
    chatMessages.add(new ChatMessage(message));
  }

  /**
   * Checks whether the game already started, is already filled, the player is already inGame and
   * the games privacy
   *
   * @param username
   * @param password
   * @return Whether the play is able/allowed to join the game
   */
  public boolean checkIfYouCanJoin(String username, String password) {
    if (hasStarted()) {
      return false;
    }
    if (getCurrentNumberOfPlayers() >= getMaxPlayers()) {
      return false;
    }
    if (getPlayers().containsKey(username)) {
      return false;
    }
    if (isPrivate()
        && !getHash().equals(Game.getHashedPassword(password, getSalt()))
        && !getHash().equals(password)) {
      return false;
    }
    return true;
  }

  /** Changes the host to the first/next player who isn't set as host */
  public void changeHost() {
    String currentHost = getHost();
    for (Map.Entry<String, Player> entry : getPlayers().entrySet()) {
      if (!entry.getValue().getUserName().equals(currentHost)) {
        setHost(entry.getValue().getUserName());
        break;
      }
    }
  }

  /**
   * Checks whether it is currently the turn of this player.
   *
   * @param player The player to be checked
   * @return <code>true</code> if it is the turn of this player, <code>false</code> otherwise
   */
  public boolean isTurnOfPlayer(Player player) {
    return turn.getCurrentPlayer() == player;
  }

  /**
   * Whether the leaving player is the last player in the current game
   *
   * @return
   */
  public boolean checkIfLastPlayer() {
    return getCurrentNumberOfPlayers() == 1;
  }

  public List<Player> getInGamePlayersList() {
    return new ArrayList<Player>(getPlayers().values());
  }

  public List<String> getInGamePlayerNames() {
    ArrayList<String> playerNames = new ArrayList<String>();
    for (Player p : getInGamePlayersList()) {
      if (host != p.getUserName()) {
        playerNames.add(p.getUserName());
      }
    }
    return playerNames;
  }

  public String getName() {
    return name;
  }

  public String getHash() {
    return hash;
  }

  public byte[] getSalt() {
    return salt;
  }

  public Turn getTurn() {
    return turn;
  }

  public LinkedList<ChatMessage> getChatMessages() {
    return chatMessages;
  }

  public int getCurrentNumberOfPlayers() {
    return players.size();
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public Map<String, Player> getPlayers() {
    return players;
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  @Override
  public String toString() {
    return "Game(Name: " + name + ", maxPlayers: " + maxPlayers + ", isPrivate: " + isPrivate + ")";
  }

  /**
   * Returns the hash of password and salt
   *
   * @param password
   * @param salt
   * @return
   */
  public static String getHashedPassword(String password, byte[] salt) {
    String hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] hashByteArray = md.digest(password.getBytes(StandardCharsets.UTF_8));
      hash = new String(hashByteArray, StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hash;
  }

  public boolean hasStarted() {
    return startingTime != null;
  }

  public void setStartingTimeForTestingOnly(LocalDateTime time) {
    startingTime = time;
  }

  public boolean hasEnded() {
    return endingTime != null;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = requireNonNull(host);
  }

  public StackAndHeap getStackAndHeap() {
    return stackAndHeap;
  }

  public void clickedOnHandCard(Player player, StackCard handCard) {
    if (turn.getCurrentPlayer() != player && turn.getStage() != TurnStage.CHOOSING_CARD_TO_BLOCK_WITH) return;

    player.setSelectedHandCard(handCard);

    try {
      if (turn.getStage() == TurnStage.CHOOSING_CARD_TO_BLOCK_WITH
          && player.getReceivedCards().contains(turn.getLastPlayedBugCard())) {

        if (((ActionCard) player.getSelectedHandCard()).isLameExcuse()
            || ((ActionCard) player.getSelectedHandCard()).isSolution()) {
          defendBugImmediately(player, (ActionCard) player.getSelectedHandCard());

          if (player.getSelectedHandCard() != null) {
            player.removeHandCard(player.getSelectedHandCard());
          }

          player.clearBugBlocks();
          turn.setAttackedPlayer(null);
          turn.setStage(Turn.TurnStage.PLAYING_CARDS);
        }
      }
    } finally {
    	
    }
  }

  /**
   * Call when a player clicked on the heap.
   *
   * @param player The player who clicked on the heap.
   * @return <code>true</code> if successfully discarded a card, <code>false</code> otherwise
   */
  public boolean clickedOnHeap(Player player) {
    if (turn.getCurrentPlayer() != player || turn.getStage() != TurnStage.DISCARDING_CARDS)
      return false;
    if (player.getSelectedHandCard() != null) {
      return discardHandCard(player, player.getSelectedHandCard(), true);
    }
    return false;
  }

  /**
   * Called when a player clicks on the "Add Card"-Button of another player. If he has selected a
   * hand card, it will be moved to the block cards of the other player. No rules are checked yet.
   *
   * @param player The player whose turn it should be.
   * @param receiverOfCard The player who should receive the previously selected card.
   */
  public void clickedOnAddCardToPlayer(Player player, Player receiverOfCard) {

    StackCard selectedCard = player.getSelectedHandCard();

    if (turn.getCurrentPlayer() != player
        || turn.getStage() != TurnStage.PLAYING_CARDS
        || selectedCard == null) {
      return;
    }

    if (selectedCard.isBug() && turn.getPlayedBugCardsInThisTurn() >= Turn.MAX_BUG_CARDS_PER_TURN) {
      chatMessages.add(
          new ChatMessage(
              "You cannot play another bug.")); // TODO: Only show player who played card?
      return;
    }

    if (((Card) selectedCard).getCardType() != CardType.ABILITY) {
      if (((Card) selectedCard).getCardType() == CardType.ACTION) {
        if ((((ActionCard) selectedCard).getAction() == Action.COFFEE_MACHINE
                || ((ActionCard) selectedCard).getAction() == Action.RED_BULL)
            && player != receiverOfCard) {
          chatMessages.add(
              new ChatMessage(
                  "You can only play a " + selectedCard.toString() + " card for yourself."));
          return;
        }
      }
      putCardToPlayer(selectedCard, player, receiverOfCard);
      statistics.playedCard(selectedCard);
    }
  }

  public void clickedOnReceivedCard(Player player, StackCard clickedCard) {
    if (!player.hasSelectedCard()) {
      return;
    }

    StackCard selectedCard = player.getSelectedHandCard();
    LOGGER.info(player.getUserName() + " clicked on received Card");
    if (((Card) selectedCard).getCardType() == CardType.ACTION) {
      if (((ActionCard) selectedCard).isLameExcuse() || ((ActionCard) selectedCard).isSolution()) {
        discardHandCard(player, selectedCard, false);
        putCardOnHeap(player, clickedCard);
        player.getReceivedCards().remove(clickedCard);
        chatMessages.add(
            new ChatMessage(player.getUserName() + " defends with " + selectedCard.toString()));
        if (player.getMentalHealthInt() < player.getCharacterCard().getMaxHealthPoints()) {
          player.addToMentalHealth(1);
        }
      }
    }
  }

  /**
   * Called when player receives a bug and clicks on a lame excuse or solution card in his hand
   * cards to defend the bug immediately.
   *
   * @param player The player who received the bug.
   * @param blockingCard Either a solution or lame excuse card.
   */
  public void defendBugImmediately(Player player, ActionCard blockingCard) {
    ActionCard bugCard = turn.getLastPlayedBugCard();

    putCardOnHeap(player, blockingCard);
    player.getReceivedCards().remove(blockingCard);
    player.getReceivedCards().remove(bugCard);

    if (player.getMentalHealthInt() < player.getCharacterCard().getMaxHealthPoints()) {
      player.addToMentalHealth(1);
    }

    chatMessages.add(
        new ChatMessage(
            player.getUserName()
                + " blocked \""
                + bugCard.toString()
                + "\" with \""
                + blockingCard.toString()
                + "\"."));

    this.setHasPlayedBugBeenDefended(true);
  }

  /**
   * Called at beginning of turn and checks whether player has to deal with stumbling block cards.
   *
   * @param player The player whose turn it should be.
   */
  public void dealWithStumblingBlocks(Player player) {
    StumblingBlockCard maintenanceCard = null;
    List<StumblingBlockCard> trainingCards = new ArrayList<StumblingBlockCard>();
    for (StackCard card : player.getReceivedCards()) {
      if (((Card) card).getCardType() == CardType.STUMBLING_BLOCK) {
        if (((StumblingBlockCard) card).getStumblingBlock() == StumblingBlock.MAINTENANCE) {
          maintenanceCard = (StumblingBlockCard) card;
        }

        if (((StumblingBlockCard) card).getStumblingBlock() == StumblingBlock.TRAINING) {
          trainingCards.add((StumblingBlockCard) card);
        }
      }
    }
    if (maintenanceCard != null) {
      dealWithMaintenance(player, maintenanceCard);
    }
    if (!trainingCards.isEmpty()) {
      for (StumblingBlockCard trainingCard : trainingCards) {
        if (dealWithTraining(player, trainingCard)) {
          turn.switchToNextPlayer();
          dealWithStumblingBlocks(turn.getCurrentPlayer());
          return;
        }
      }
    }
  }

  /**
   * Called when player has to deal with fortran maintenance card.
   *
   * @param player The player whose turn it should be.
   * @param maintenanceCard The fortran maintenance card which has to be dealt with.
   */
  public void dealWithMaintenance(Player player, StumblingBlockCard maintenanceCard) {
    if (player.hasToDoFortranMaintenance()) {
      player.addToMentalHealth(-3);
      stackAndHeap.addToHeap(maintenanceCard, player, false);
      chatMessages.add(
          new ChatMessage(
              player.getUserName()
                  + " has to do Fortran Maintenance and lost 3 Mental Health Points."));
    } else {
      Player p = turn.getNextPlayer(turn.getCurrentPlayerIndex());
      p.receiveCard(maintenanceCard);
      chatMessages.add(
          new ChatMessage(
              player.getUserName()
                  + " doesn't have to do Fortran Maintenance and card moves to "
                  + p.getUserName()
                  + "."));
    }
    player.removeReceivedCard(maintenanceCard);
  }

  /**
   * Called when player has to deal with off-the-job-training card. Returns true if training takes
   * place, false if training cancelled.
   *
   * @param player The player whose turn it should be.
   * @param trainingCard The off-the-job-training card which has to be dealt with.
   */
  public boolean dealWithTraining(Player player, StumblingBlockCard trainingCard) {
    stackAndHeap.addToHeap(trainingCard, player, false);
    player.removeReceivedCard(trainingCard);
    if (player.hasToDoOffTheJobTraining()) {
      chatMessages.add(
          new ChatMessage(
              player.getUserName()
                  + " has to do an off the job training and has to skip his/her turn."));
      return true;

    } else {
      chatMessages.add(
          new ChatMessage(player.getUserName() + " doesn't have to do an off the job training."));
      return false;
    }
  }

  /**
   * Called when a player clicks on the "Play Ability"-Button. If he has selected a hand card of the
   * type ability, it will be moved to his uncovered cards. No rules are checked yet.
   *
   * @param player The player whose turn it should be.
   * @param receiverOfCard
   */
  public void clickedOnPlayAbility(Player player, Player receiverOfCard) {
    if (turn.getCurrentPlayer() != player || turn.getStage() != TurnStage.PLAYING_CARDS) return;
    StackCard selectedCard = player.getSelectedHandCard();
    if (selectedCard != null && selectedCard instanceof AbilityCard) {
      if (player.removeHandCard(selectedCard)) {
        receiverOfCard.addPlayedAbilityCard((AbilityCard) selectedCard);
        statistics.playedCard(selectedCard);
      }
    }
  }

  /**
   * A player clicked on the discard button to initiate the discarding of surplus hand cards.
   *
   * @param player The player who clicked on the button.
   */
  public void clickedOnDiscardButton(Player player) {
    if (turn.getCurrentPlayer() != player || turn.getStage() != TurnStage.PLAYING_CARDS) {
      LOGGER.debug("Player clicked on discard button but not his turn or not in the right stage");
      return;
    }
    turn.setStage(TurnStage.DISCARDING_CARDS);
  }

  /**
   * A player clicked on the draw cards button to get two cards from the stack.
   *
   * @param player The player who clicked on the button.
   */
  public void clickedOnDrawCardsButton(Player player) {
    if (turn.getCurrentPlayer() != player || turn.getStage() != TurnStage.DRAWING_CARDS) {
      LOGGER.debug(
          "Player clicked on draw cards button but not his turn or not in the right stage");
      return;
    }
    drawCardsFromStack(player);
    turn.setStage(TurnStage.PLAYING_CARDS);
  }

  /**
   * A player clicked on the end turn button.
   *
   * @param player The player who clicked on the button.
   */
  public void clickedOnEndTurnButton(Player player) {
    if (turn.getCurrentPlayer() != player
        || !(turn.getStage() == TurnStage.DISCARDING_CARDS
            || turn.getStage() == TurnStage.PLAYING_CARDS)) {
      LOGGER.debug("Player clicked on end turn button but not his turn or not in the right stage");
      return;
    }
    if (player.canEndTurn()) {
      saveMentalHealthInfo();
      turn.switchToNextPlayer();
      dealWithStumblingBlocks(turn.getCurrentPlayer());
    }
  }

  private void drawCardsFromStack(Player player) {
    player.addToHandCards(stackAndHeap.drawCard());
    player.addToHandCards(stackAndHeap.drawCard());
  }

  public void firePlayer(Player player, Set<StackCard> handCards) {
    stackAndHeap.addAllToHeap(handCards, player);
    handCards.removeAll(handCards);
    player.fire(true);

    if (developer != null) {
      if (manager.isFired()
          || consultant.isFired() && allMonkeysFired(monkeys)
          || manager.isFired() && allMonkeysFired(monkeys) && developer.isFired()) {
        endGame();
      }
    } else {
      if (manager.isFired()
          || consultant.isFired() && allMonkeysFired(monkeys)
          || manager.isFired() && allMonkeysFired(monkeys)) {
        endGame();
      }
    }
  }

  public boolean allMonkeysFired(List<Player> monkeys) {
    for (Player monkey : monkeys) {
      if (!monkey.isFired()) {
        return false;
      }
    }
    return true;
  }

  public boolean allMonkeysWon(List<Player> monkeys) {
    for (Player monkey : monkeys) {
      if (!monkey.hasWon()) {
        return false;
      }
    }
    return true;
  }

  public Player getManager() {
    return manager;
  }

  public Player getConsultant() {
    return consultant;
  }

  public List<Player> getEvilCodeMonkeys() {
    return monkeys;
  }

  public Player getHonestDeveloper() {
    return developer;
  }

  /** Ends the game. */
  public synchronized void endGame() {
    if (hasEnded()) {
      return;
    }
    endingTime = LocalDateTime.now();

    // Consultant wins
    if (manager.isFired() && allMonkeysFired(monkeys)) {
      consultant.win(true);
      manager.win(false);
      for (Player monkey : monkeys) {
        monkey.win(false);
      }
      if (developer != null) {
        developer.win(false);
      }
    }
    // Evil Code Monkeys win
    else if (manager.isFired()) {
      for (Player monkey : monkeys) {
        monkey.win(true);
      }
      manager.win(false);
      consultant.win(false);
      if (developer != null) {
        developer.win(false);
      }
    }
    // Manager and Honest Developers win
    else if (consultant.isFired() && allMonkeysFired(monkeys)) {
      manager.win(true);
      consultant.win(false);
      for (Player monkey : monkeys) {
        monkey.win(false);
      }
      if (developer != null) {
        developer.win(true);
      }
    }
  }

  public String getWinners(Player basePlayer) {
    winners = "";
    // Consultant wins
    if (consultant.hasWon()) {
      if (basePlayer.getRole() == Role.CONSULTANT) {
        winners = "You have won.";
      } else {
        winners = consultant.getUserName() + " has won.";
      }
    }
    // Evil Code Monkeys win
    else if (allMonkeysWon(monkeys)) {
      for (Player monkey : monkeys) {
        if (basePlayer.getUserName() == monkey.getUserName()) {
          winners += "You, ";
        } else {
          winners += monkey.getUserName() + ", ";
        }
      }
      // remove , at end of string
      winners = winners.substring(0, winners.length() - 2);
      winners += " have won.";
    }
    // Manager and Honest Developers win
    else if (manager.hasWon()) {
      if (developer == null) {
        if (basePlayer.getRole() == Role.MANAGER) {
          winners = "You have won.";
        } else {
          winners = manager.getUserName() + " has won.";
        }
      } else {
        if (basePlayer.getRole() == Role.HONEST_DEVELOPER) {
          winners += "You";
          winners += " & " + manager.getUserName() + " have won.";
        } else if (basePlayer.getRole() == Role.MANAGER) {
          winners += developer.getUserName();
          winners += " & you have won.";
        } else {
          winners += developer.getUserName();
          winners += " & " + manager.getUserName() + " have won.";
        }
      }
    }
    return winners;
  }

  public String getGroupWon() {
    if (manager.hasWon() && developer == null) {
      return "The Manager has won!";
    } else if (consultant.hasWon()) {
      return "The Consultant has won!";
    } else if (allMonkeysWon(monkeys)) {
      return "The Evil Code Monkeys have won!";
    } else return "The Manager and the Honest Developer have won!";
  }

  /** Each player saves his current count of mental health points. */
  private void saveMentalHealthInfo() {
    for (Player player : players.values()) {
      player.snapshotOfMentalHealth();
    }
  }

  public String getStartingTimeAsString() {
    if (endingTime == null) {
      return "Game has not started yet.";
    }
    return startingTime.format(timeFormatter);
  }

  public String getEndingTimeAsString() {
    if (endingTime == null) {
      return "Game not over yet.";
    }
    return endingTime.format(timeFormatter);
  }

  public String getDurationAsString() {
    if (endingTime == null || startingTime == null) {
      return "Game not over yet.";
    }
    Duration duration = Duration.between(startingTime, endingTime);
    return duration.toHours()
        + "h "
        + duration.toMinutesPart()
        + "min "
        + duration.toSecondsPart()
        + "s";
  }

  public GameStatistics getStatistics() {
    return statistics;
  }

  public boolean getHasPlayedBugBeenDefended() {
    return hasPlayedBugBeenDefended;
  }

  public void setHasPlayedBugBeenDefended(boolean hasPlayedBugBeenDefended) {
    this.hasPlayedBugBeenDefended = hasPlayedBugBeenDefended;
  }
}

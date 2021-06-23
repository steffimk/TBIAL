package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.game.AbilityCard;
import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.ActionCard;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;
import de.lmu.ifi.sosy.tbial.game.StackAndHeap;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;

public class GameTableTest extends PageTestBase {

  private Game game;
  private Player basePlayer;
  private Player playerA;
  private Player playerB;
  private Player playerC;

  private StackCard clickedOnHandCard;
  private Player receivingPlayer;

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
    getSession().authenticate("testuser", "testpassword");
    game = new Game("gamename", 4, true, "123456", "testuser");
    // Add players so that game can be started
    game.addNewPlayer("A");
    game.addNewPlayer("B");
    game.addNewPlayer("C");
    getGameManager().addGame(game);
    game.startGame();
    basePlayer = game.getPlayers().get("testuser");
    playerA = game.getPlayers().get("A");
    playerB = game.getPlayers().get("B");
    playerC = game.getPlayers().get("C");
  }

  @Test
  public void gameTableHasComponents() {
    tester.startPage(GameTable.class);
    tester.assertComponent("gameName", Label.class);
    tester.assertModelValue("gameName", game.getName());

    // basePlayer
    tester.assertComponent("table:player1:panel1", PlayerAreaPanel.class);
    tester.assertModelValue("table:player1:panel1", basePlayer);
    tester.assertComponent("table:player1:panel1:playAbilityButton", AjaxLink.class);

    tester.assertComponent("table:player1:panel1:userName", Label.class);
    tester.assertModelValue("table:player1:panel1:userName", "testuser");
    tester.assertComponent("table:player1:panel1:mentalHealth", Label.class);
    tester.assertModelValue("table:player1:panel1:mentalHealth", basePlayer.getMentalHealth());
    tester.assertComponent("table:player1:panel1:prestige", Label.class);
    tester.assertModelValue("table:player1:panel1:prestige", basePlayer.getPrestige());
    tester.assertComponent("table:player1:panel1:roleName", Label.class);
    tester.assertModelValue("table:player1:panel1:roleName", basePlayer.getRoleName());

    @SuppressWarnings("unchecked")
    ListView<StackCard> handCardsListViewBase =
        (ListView<StackCard>)
            tester.getComponentFromLastRenderedPage(
                "table:player1:panel1:handCardContainer:handcards");
    // Check if the hand cards of the base player and hand cards in panel1 contain the same cards
    assertTrue(handCardsListViewBase.getModelObject().containsAll(basePlayer.getHandCards()));
    assertTrue(basePlayer.getHandCards().containsAll(handCardsListViewBase.getModelObject()));

    handCardsListViewBase.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<StackCard>, Void>() {

          @Override
          public void component(ListItem<StackCard> card, IVisit<Void> visit) {
            tester.assertComponent(card.getPath().substring(2) + ":handCard", Image.class);
            tester.assertContains(
                card.getModelObject()
                    .getResourceFileName()
                    .substring(0, card.getModelObject().getResourceFileName().length() - 4));
          }
        });

    // other 3 players

    @SuppressWarnings("unchecked")
    ListView<Player> playerListView =
        (ListView<Player>) tester.getComponentFromLastRenderedPage("table:container");

    playerListView.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<Player>, Void>() {

          @Override
          public void component(ListItem<Player> player, IVisit<Void> visit) {
            tester.assertComponent(player.getPath().substring(2) + ":panel", PlayerAreaPanel.class);
            tester.assertModelValue(
                player.getPath().substring(2) + ":panel", player.getModelObject());
            tester.assertComponent(player.getPath().substring(2) + ":panel:userName", Label.class);
            tester.assertModelValue(
                player.getPath().substring(2) + ":panel:userName",
                player.getModelObject().getUserName());
            tester.assertComponent(
                player.getPath().substring(2) + ":panel:mentalHealth", Label.class);
            tester.assertModelValue(
                player.getPath().substring(2) + ":panel:mentalHealth",
                player.getModelObject().getMentalHealth());
            tester.assertComponent(player.getPath().substring(2) + ":panel:prestige", Label.class);
            tester.assertModelValue(
                player.getPath().substring(2) + ":panel:prestige",
                player.getModelObject().getPrestige());
            // only visible if role = manager
            if (player.getModelObject().getRole() == Role.MANAGER) {
              tester.assertComponent(
                  player.getPath().substring(2) + ":panel:roleName", Label.class);
              tester.assertModelValue(
                  player.getPath().substring(2) + ":panel:roleName",
                  player.getModelObject().getRoleName());
            }
            @SuppressWarnings("unchecked")
            ListView<StackCard> handCardsListView =
                (ListView<StackCard>)
                    tester.getComponentFromLastRenderedPage(
                        player.getPath().substring(2) + ":panel:handCardContainer:handcards");
            // Check if the hand cards of the player and hand cards in panel contain the same cards
            assertTrue(
                handCardsListView
                    .getModelObject()
                    .containsAll(player.getModelObject().getHandCards()));
            assertTrue(
                player
                    .getModelObject()
                    .getHandCards()
                    .containsAll(handCardsListView.getModelObject()));

            handCardsListView.visitChildren(
                ListItem.class,
                new IVisitor<ListItem<StackCard>, Void>() {

                  @Override
                  public void component(ListItem<StackCard> card, IVisit<Void> visit) {
                    tester.assertComponent(card.getPath().substring(2) + ":handCard", Image.class);
                    tester.assertContains("imgs/cards/backSide");
                  }
                });
            visit.dontGoDeeper();
          }
        });

    // Stack and Heap
    tester.assertComponent("table:stackContainer", WebMarkupContainer.class);
    tester.assertComponent("table:heapContainer", WebMarkupContainer.class);

    // Turn related buttons
    tester.assertComponent("playCardsButton", AjaxLink.class);
    tester.assertComponent("discardButton", AjaxLink.class);
    tester.assertComponent("endTurnButton", AjaxLink.class);

    // Chat
    tester.assertComponent("chatPanel", ChatPanel.class);
  }

  /**
   * This tests if player gets fired if his/her mental health is 0 and thus the role of the player
   * is shown to everyone on the game table.
   */
  @Test
  public void showRoleAfterPlayerIsFired() {
    tester.startPage(GameTable.class);

    // fire all three players
    playerA.setMentalHealth(0);
    playerB.setMentalHealth(0);
    playerC.setMentalHealth(0);
    // need to click on message container in order for it to be initialized because of executing all
    // timer behaviors
    tester.clickLink("messageContainer:message");
    tester.executeAllTimerBehaviors(tester.getLastRenderedPage());

    // Player A
    assertTrue(playerA.isFired());
    tester.assertComponent("table:container:0:panel:roleName", Label.class);
    tester.assertModelValue("table:container:0:panel:roleName", playerA.getRoleName());

    // Player B
    assertTrue(playerB.isFired());
    tester.assertComponent("table:container:1:panel:roleName", Label.class);
    tester.assertModelValue("table:container:1:panel:roleName", playerB.getRoleName());

    // Player C
    assertTrue(playerC.isFired());
    tester.assertComponent("table:container:2:panel:roleName", Label.class);
    tester.assertModelValue("table:container:2:panel:roleName", playerC.getRoleName());
  }

  @Test
  public void clickOnHeapTriggersClickedOnHeapInGame() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.DISCARDING_CARDS);
    ArrayList<StackCard> handCards = new ArrayList<>(basePlayer.getHandCards());
    StackCard testCard = handCards.get(0);
    basePlayer.setSelectedHandCard(testCard);
    List<StackCard> heap = game.getStackAndHeap().getHeap();

    tester.executeAjaxEvent("table:heapContainer", "click");
    // Card should be on heap now
    assertTrue(heap.contains(testCard));
    assertNull(basePlayer.getSelectedHandCard());

    int heapSizeBefore = heap.size();
    tester.executeAjaxEvent("table:heapContainer", "click");
    // No card should have been added to the heap
    assertEquals(heapSizeBefore, heap.size());
  }

  /**
   * This tests if clicking on a hand cards triggers methods resulting in the selectedHandCard of
   * the base player being the clicked on hand card
   */
  @SuppressWarnings("unchecked")
  @Test
  public void clickOnHandCardTriggersSelectHandCard() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);
    ListView<StackCard> handCardsListView =
        (ListView<StackCard>)
            tester.getComponentFromLastRenderedPage(
                "table:player1:panel1:handCardContainer:handcards");
    // Check if the hand cards of the base player and hand cards in panel1 contain the same cards
    assertTrue(handCardsListView.getModelObject().containsAll(basePlayer.getHandCards()));
    assertTrue(basePlayer.getHandCards().containsAll(handCardsListView.getModelObject()));

    handCardsListView.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<StackCard>, Void>() {

          @Override
          public void component(ListItem<StackCard> item, IVisit<Void> visit) {
            // Click on first card in hand cards and don't visit other cards
            tester.executeAjaxEvent(item, "click");
            clickedOnHandCard = item.getModelObject();
            visit.dontGoDeeper();
          }
        });

    assertEquals(basePlayer.getSelectedHandCard(), clickedOnHandCard);
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void clickOnPlayAbilityWorks() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    AbilityCard testCard = new AbilityCard(Ability.GOOGLE);
    basePlayer.addToHandCards(testCard);
    basePlayer.setSelectedHandCard(testCard);

    tester.clickLink("table:player1:panel1:playAbilityButton");

    assertTrue(basePlayer.getPlayedAbilityCards().contains(testCard));
    assertNull(basePlayer.getSelectedHandCard());

    ActionCard testCardShouldFail = new ActionCard(Action.COFFEE_MACHINE);
    basePlayer.addToHandCards(testCardShouldFail);
    basePlayer.setSelectedHandCard(testCardShouldFail);

    tester.clickLink("table:player1:panel1:playAbilityButton");

    assertFalse(basePlayer.getPlayedAbilityCards().contains(testCardShouldFail));
    assertEquals(basePlayer.getSelectedHandCard(), testCardShouldFail);

    AbilityCard bugDelegationCard = new AbilityCard(Ability.BUG_DELEGATION);
    basePlayer.addToHandCards(bugDelegationCard);
    basePlayer.setSelectedHandCard(bugDelegationCard);

    tester.clickLink("table:container:0:panel:playAbilityButton");

    receivingPlayer =
        (Player)
            ((PlayerAreaPanel) tester.getComponentFromLastRenderedPage("table:container:0:panel"))
                .getDefaultModelObject();

    assertFalse(basePlayer.getHandCards().contains(bugDelegationCard));
    assertNull(basePlayer.getSelectedHandCard());
    assertTrue(receivingPlayer.getPlayedAbilityCards().contains(bugDelegationCard));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void clickOnAddCardLetsPlayerReceiveCard() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);
    
    // Testing with ActionCard because Ability-Bug might get blocked
    ActionCard testCard = new ActionCard(Action.BORING);
    basePlayer.addToHandCards(testCard);
    basePlayer.setSelectedHandCard(testCard);

    ListView<Player> playerPanelListView =
        (ListView<Player>) tester.getComponentFromLastRenderedPage("table:container");
    // Check if all players in ListView are players of the game
    assertTrue(game.getPlayers().values().containsAll(playerPanelListView.getModelObject()));

    playerPanelListView.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<Player>, Void>() {

          @Override
          public void component(ListItem<Player> item, IVisit<Void> visit) {
            receivingPlayer = item.getModelObject();
            AjaxLink<Void> link = (AjaxLink<Void>) item.get("panel:addCardButton");
            tester.clickLink(link);
            visit.dontGoDeeper();
          }
        });

    assertTrue(receivingPlayer.getReceivedCards().contains(testCard));
    assertFalse(basePlayer.getHandCards().contains(testCard));

    // Make sure that ability cards do not get added to the "played cards" area of players
    AbilityCard testCardShouldFail = new AbilityCard(Ability.ACCENTURE);
    basePlayer.addToHandCards(testCardShouldFail);
    basePlayer.setSelectedHandCard(testCardShouldFail);

    tester.clickLink("table:container:0:panel:addCardButton");

    receivingPlayer =
        (Player)
            ((PlayerAreaPanel) tester.getComponentFromLastRenderedPage("table:container:0:panel"))
                .getDefaultModelObject();

    assertTrue(basePlayer.getHandCards().contains(testCardShouldFail));
    assertFalse(receivingPlayer.getReceivedCards().contains(testCardShouldFail));
  }

  @Test
  public void clickOnPlayCardButtonWorks() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.DRAWING_CARDS);

    for (int i = 0; i < Turn.DRAW_LIMIT_IN_DRAWING_STAGE; i++) {
      game.getTurn().incrementDrawnCardsInDrawingStage();
    }

    tester.clickLink(tester.getComponentFromLastRenderedPage("playCardsButton"));

    assertEquals(game.getTurn().getStage(), TurnStage.PLAYING_CARDS);
    
  }

  @Test
  public void clickOnDiscardButtonWorks() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    tester.clickLink(tester.getComponentFromLastRenderedPage("discardButton"));

    assertEquals(game.getTurn().getStage(), TurnStage.DISCARDING_CARDS);
  }

  @Test
  public void drawCardsFromStackWorks() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.DRAWING_CARDS);

    ArrayList<StackCard> handCards = new ArrayList<>(basePlayer.getHandCards());
    int handSizeBefore = handCards.size();

    List<StackCard> stack = game.getStackAndHeap().getStack();
    int stackSizeBefore = stack.size();

    // Draw two cards from stack
    tester.executeAjaxEvent("table:stackContainer", "click");
    tester.executeAjaxEvent("table:stackContainer", "click");

    // stack size should be decreased by two
    assertEquals(stackSizeBefore - 2, stack.size());
    // hand size should be increased by two
    assertEquals(handSizeBefore + 2, basePlayer.getHandCards().size());
  }

  @Test
  public void clickOnEndTurnButtonWorks() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.DISCARDING_CARDS);
    int mentalHealth = basePlayer.getMentalHealthInt();
    int handCardSize = basePlayer.getHandCards().size();

    // Make mental health smaller than amount of hand cards -> player cannot end turn
    basePlayer.addToMentalHealth(-mentalHealth);
    tester.clickLink(tester.getComponentFromLastRenderedPage("endTurnButton"));
    
    assertEquals(game.getTurn().getCurrentPlayer(), basePlayer);
    assertEquals(game.getTurn().getStage(), TurnStage.DISCARDING_CARDS);

    // Make mental health bigger than amount of hand cards -> player can end turn
    basePlayer.addToMentalHealth(handCardSize + 1);
    tester.clickLink(tester.getComponentFromLastRenderedPage("endTurnButton"));
    
    assertFalse(game.getTurn().getCurrentPlayer() == basePlayer);
    assertEquals(game.getTurn().getStage(), TurnStage.DRAWING_CARDS);
  }

  @Test
  public void chatIsWorking() {
    tester.startPage(GameTable.class);

    // check if chat is empty at start of game
    assertEquals(game.getChatMessages().isEmpty(), true);

    // basePlayer writing (also empty) messages
    FormTester form = tester.newFormTester("chatPanel:form");
    form.setValue("message", "hallo");
    form.submit("send");
    form.setValue("message", "");
    form.submit("send");
    form.setValue("message", "hey");
    form.submit("send");

    // check if messages get displayed
    @SuppressWarnings("unchecked")
    ListView<ChatMessage> messageList =
        (ListView<ChatMessage>)
            tester.getComponentFromLastRenderedPage("chatPanel:chatMessages:messages");

    messageList.visitChildren(
        ListItem.class,
        new IVisitor<ListItem<ChatMessage>, Void>() {

          @Override
          public void component(ListItem<ChatMessage> item, IVisit<Void> visit) {
            tester.assertComponent(item.getPath().substring(2) + ":sender", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":sender", item.getModelObject().getSender());
            tester.assertComponent(item.getPath().substring(2) + ":textMessage", Label.class);
            tester.assertModelValue(
                item.getPath().substring(2) + ":textMessage",
                item.getModelObject().getTextMessage());
            visit.dontGoDeeper();
          }
        });

    // check if only the two not empty messages were added
    assertEquals(game.getChatMessages().size(), 2);
  }
}

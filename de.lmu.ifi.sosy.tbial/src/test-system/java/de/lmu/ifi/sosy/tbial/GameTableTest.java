package de.lmu.ifi.sosy.tbial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;

public class GameTableTest extends PageTestBase {

  private Game game;
  private Player basePlayer;

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
    getSession().setCurrentGame(game);
    game.startGame();
    basePlayer = game.getPlayers().get("testuser");
  }

  @Test
  public void gameTableHasComponents() {
    tester.startPage(GameTable.class);
    tester.assertComponent("gameName", Label.class);
    tester.assertModelValue("gameName", game.getName());
    tester.assertComponent("table:player1:panel1", PlayerAreaPanel.class);
    tester.assertModelValue("table:player1:panel1", basePlayer);
    tester.assertComponent("table:player1:panel1:playAbilityButton", AjaxLink.class);
    // TODO T6: Game Table
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

  @SuppressWarnings("unchecked")
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
  }

  @SuppressWarnings("unchecked")
  @Test
  public void clickOnAddCardLetsPlayerReceiveCard() {
    tester.startPage(GameTable.class);
    game.getTurn().setTurnPlayerUseForTestingOnly(basePlayer);
    game.getTurn().setStage(TurnStage.PLAYING_CARDS);

    ArrayList<StackCard> handCards = new ArrayList<>(basePlayer.getHandCards());
    StackCard testCard = handCards.get(0);
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
  }
}

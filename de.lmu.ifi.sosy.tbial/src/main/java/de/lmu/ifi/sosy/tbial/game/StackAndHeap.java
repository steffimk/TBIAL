package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;

/**
 * The Stack and Heap of the game. Stack contains all action, ability and stumbling block cards when
 * initializing, while Heap is empty in the beginning.
 */
public class StackAndHeap implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<StackCard> stack;
  private List<StackCard> heap;
  public static final int STACK_SIZE_AT_START = 64; // 80 TODO: Adapt when implementing more cards.
  public static final int HEAP_MAX_SIZE = 64; // 80 TODO: Adapt when implementing more cards.80
  /** The last player to add a card to the heap. Needed for the animation of the discard */
  private Player lastPlayerToDiscardCard;

  private boolean wasNormalDiscard;

  public StackAndHeap() {
    this.stack = Collections.synchronizedList(new LinkedList<StackCard>());
    this.heap = Collections.synchronizedList(new LinkedList<StackCard>());
    
    addAllCards();
    Collections.shuffle(stack);
  }

  /** Adds all cards to the stack. */
  private void addAllCards() {
    // Add Ability Cards
    for (Ability ability : Ability.values()) {
      for (int i = 0; i < ability.count; i++) {
        stack.add(new AbilityCard(ability));
      }
    }
    // Add Action Cards
    for (Action action : Action.values()) {
      if (action.isImplemented) {
        for (int i = 0; i < action.count; i++) {
          stack.add(new ActionCard(action));
        }
      }
    }
    // Add Stumbling Block Cards
    for (StumblingBlock stumblingBlock : StumblingBlock.values()) {
      for (int i = 0; i < stumblingBlock.count; i++) {
        stack.add(new StumblingBlockCard(stumblingBlock));
      }
    }
  }

  /**
   * Returns the first card in the stack and removes it from the stack.
   *
   * @return the first card on the stack
   */
  public StackCard drawCard() {
    if (stack.size() == 0) {
      refillStack();
    }
    return stack.remove(0);
  }

  /** Refills empty Stack with cards from heap. */
  public void refillStack() {
    for (StackCard card : heap) {
      stack.add(card);
    }
    
    heap.clear();
    
    Collections.shuffle(stack);
  }

  /**
   * Adds the card to the heap
   *
   * @param card The card to be added to the heap
   * @param player The player from whose area the card is added to the heap
   * @param isNormalDiscard <code>true</code> if the card is added to the heap in the discard stage
   */
  public void addToHeap(StackCard card, Player player, boolean isNormalDiscard) {
    heap.add(card);
    lastPlayerToDiscardCard = player;
    wasNormalDiscard = isNormalDiscard;
  }

  /**
   * Adds all handCards to the heap
   *
   * @param cards The cards to be added to the heap
   */
  public synchronized void addAllToHeap(Set<StackCard> cards, Player player) {
    heap.addAll(cards);
    lastPlayerToDiscardCard = player;
  }

  public List<StackCard> getStack() {
    return stack;
  }

  /**
   * Returns the uppermost card of the heap, that is the most recently discarded card.
   *
   * @return The uppermost card of the heap or <code>null</code> if the heap is empty
   */
  public StackCard getUppermostCardOfHeap() {
    if (heap.isEmpty()) return null;
    return heap.get(heap.size() - 1);
  }

  public List<StackCard> getHeap() {
    return heap;
  }

  /**
   * Get the last player to add a card to the heap. Needed for the animation of the discard
   *
   * @return The last player to add a card to the heap.
   */
  public Player getLastPlayerToDiscardCard() {
    return lastPlayerToDiscardCard;
  }

  public int getStackSizeAtStart() {
    return STACK_SIZE_AT_START;
  }

  public int getHeapMaxSize() {
    return HEAP_MAX_SIZE;
  }

  /**
   * Information needed to trigger or not trigger an animation of the discard for the base player
   *
   * @return <code>true</code> if the last card added to the heap was a normal discard and <code>
   *     false</code> otherwise.
   */
  public boolean wasNormalDiscard() {
    return wasNormalDiscard;
  }
}

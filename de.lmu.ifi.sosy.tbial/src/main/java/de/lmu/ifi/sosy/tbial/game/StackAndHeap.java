package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
  public static final int STACK_SIZE_AT_START = 80;
  public static final int HEAP_MAX_SIZE = 80;
  /** The last player to add a card to the heap. Needed for the animation of the discard */
  private Player lastPlayerToDiscardCard;

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
      for (int i = 0; i < action.count; i++) {
        stack.add(new ActionCard(action));
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
   */
  public void addToHeap(StackCard card, Player player) {
    heap.add(card);
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
}

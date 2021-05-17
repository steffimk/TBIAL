package de.lmu.ifi.sosy.tbial.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.sosy.tbial.game.AbilityCard.Ability;
import de.lmu.ifi.sosy.tbial.game.ActionCard.Action;
import de.lmu.ifi.sosy.tbial.game.StumblingBlockCard.StumblingBlock;

/**
 * The Stack of the game. Contains all action, ability and stumbling block cards when initializing.
 */
public class Stack implements Serializable {

  private static final long serialVersionUID = 1L;

  List<StackCard> cards;

  public Stack() {
    this.cards = Collections.synchronizedList(new LinkedList<StackCard>());

    addAllCards();
    Collections.shuffle(cards);
  }

  /** Adds all cards to the stack. */
  private void addAllCards() {
    // Add Ability Cards
    for (Ability ability : Ability.values()) {
      for (int i = 0; i < ability.count; i++) {
        cards.add(new AbilityCard(ability));
      }
    }
    // Add Action Cards
    for (Action action : Action.values()) {
      for (int i = 0; i < action.count; i++) {
        cards.add(new ActionCard(action));
      }
    }
    // Add Stumbling Block Cards
    for (StumblingBlock stumblingBlock : StumblingBlock.values()) {
      for (int i = 0; i < stumblingBlock.count; i++) {
        cards.add(new StumblingBlockCard(stumblingBlock));
      }
    }
  }

  /**
   * Returns the first card in the stack and removes it from the stack.
   *
   * @return the first card on the stack
   */
  public StackCard drawCard() {
    return cards.remove(0);
  }
}
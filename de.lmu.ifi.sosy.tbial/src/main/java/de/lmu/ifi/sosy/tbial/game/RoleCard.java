package de.lmu.ifi.sosy.tbial.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Role Cards are modeled with this class. */
public class RoleCard extends Card {

  /** */
  private static final long serialVersionUID = 1L;

  private final Role role;

  public RoleCard(Role role) {
    super(CardType.ROLE);
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public String getLabel() {
    return role.label;
  }

  @Override
  public String getResourceFileName() {
    return role.fileName;
  }

  /**
   * Returns a list of count role cards in random order
   * @param count - the amount of role cards to be returned (3 < count < 8)
   * @return A list containing count role cards in random order
   */
  public static List<RoleCard> getRoleCards(int count) {
    ArrayList<RoleCard> cards = new ArrayList<>();
    if (count < 4) count = 4;
    if (count > 7) count = 7;
    for (int i = 0; i < count; i++) {
      cards.add(new RoleCard(ROLES_FOR_PLAYER_COUNT[i]));
    }
    Collections.shuffle(cards);
    return cards.subList(0, count);
  }

  /** Enum containing information about the specific role cards. */
  public enum Role {
    MANAGER("Manager", "imgs/cards/card20.png"),
    HONEST_DEVELOPER("Honest Developer", "imgs/cards/card33.png"),
    EVIL_CODE_MONKEY("Evil Code Monkey", "imgs/cards/card44.png"),
    CONSULTANT("Consultant", "imgs/cards/card21.png");

    private final String label;
    public final String fileName;

    private Role(String label, String fileName) {
      this.label = label;
      this.fileName = fileName;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

  /**
   * The order of this array displays the roles that exist in a game depending on the player count.
   * If the game has four players, the first four roles are in the game, ...
   */
  private static final Role[] ROLES_FOR_PLAYER_COUNT = {
    Role.MANAGER,
    Role.CONSULTANT,
    Role.EVIL_CODE_MONKEY,
    Role.EVIL_CODE_MONKEY,
    Role.HONEST_DEVELOPER,
    Role.EVIL_CODE_MONKEY,
    Role.EVIL_CODE_MONKEY
  };
}

package de.lmu.ifi.sosy.tbial.game;

/** Action Cards are modeled with this class. */
public class ActionCard extends Card implements StackCard {

  private final Action action;

  public ActionCard(Action action) {
    super(CardType.ACTION);
    this.action = action;
  }

  public Action getAction() {
    return action;
  }

  /** Enum containing information about the specific action cards. */
  public enum Action {
    RED_BULL("Red Bull Dispenser", 1, ActionType.SPECIAL),
    HEISENBUG("Heisenbug", 1, ActionType.SPECIAL),
    LAN("LAN Party", 1, ActionType.SPECIAL),
    COFFEE("Coffee", 2, ActionType.SOLUTION),
    CODE_FIX("Code+Fix Session", 2, ActionType.SOLUTION),
    REGEX("I know regular expressions", 2, ActionType.SOLUTION),
    STAND_UP("Standup Meeting", 2, ActionType.SPECIAL),
    COFFEE_MACHINE("Presonal Coffee Machine", 2, ActionType.SPECIAL),
    BORING("Boring Meeting", 2, ActionType.SPECIAL),
    REFACTORED("I refactored your code. Away.", 4, ActionType.SPECIAL),
    PWND("Pwnd", 4, ActionType.SPECIAL),
    SYS_INT("System Integration", 3, ActionType.SPECIAL),
    WORKS("Works for me!", 4, ActionType.LAME_EXCUSE),
    FEATURE("It's a Feature", 4, ActionType.LAME_EXCUSE),
    NOT_RESP("I'm not Responsible!", 4, ActionType.LAME_EXCUSE),
    NULLPOINTER("Nullpointer!", 4, ActionType.BUG),
    OFF_BY_ONE("Off By One!", 4, ActionType.BUG),
    NOT_FOUND("Class Not Found!", 4, ActionType.BUG),
    SYS_HANGS("System Hangs!", 4, ActionType.BUG),
    CORE_DUMP("Core Dump!", 4, ActionType.BUG),
    HATES_UI("Customer hates UI!", 4, ActionType.BUG);

    public final String label;
    public final int count;
    public final ActionType actionType;

    private Action(String label, int count, ActionType actionType) {
      this.label = label;
      this.count = count;
      this.actionType = actionType;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

  public enum ActionType {
    BUG,
    LAME_EXCUSE,
    SOLUTION,
    SPECIAL;
  }
}

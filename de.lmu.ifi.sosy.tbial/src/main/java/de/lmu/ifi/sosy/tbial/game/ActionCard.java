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

  @Override
  public String getResourceFileName() {
    return action.fileName;
  }

  /** Enum containing information about the specific action cards. */
  public enum Action {
    RED_BULL("Red Bull Dispenser", 1, ActionType.SPECIAL, "card14.pdf"),
    HEISENBUG("Heisenbug", 1, ActionType.SPECIAL, "card15.pdf"),
    LAN("LAN Party", 1, ActionType.SPECIAL, "card16.pdf"),
    COFFEE("Coffee", 2, ActionType.SOLUTION, "card22.pdf"),
    CODE_FIX("Code+Fix Session", 2, ActionType.SOLUTION, "card23.pdf"),
    REGEX("I know regular expressions", 2, ActionType.SOLUTION, "card24.pdf"),
    STAND_UP("Standup Meeting", 2, ActionType.SPECIAL, "card25.pdf"),
    COFFEE_MACHINE("Presonal Coffee Machine", 2, ActionType.SPECIAL, "card26.pdf"),
    BORING("Boring Meeting", 2, ActionType.SPECIAL, "card28.pdf"),
    REFACTORED("I refactored your code. Away.", 4, ActionType.SPECIAL, "card29.pdf"),
    PWND("Pwnd", 4, ActionType.SPECIAL, "card30.pdf"),
    SYS_INT("System Integration", 3, ActionType.SPECIAL, "card43.pdf"),
    WORKS("Works for me!", 4, ActionType.LAME_EXCUSE, "card39.pdf"),
    FEATURE("It's a Feature", 4, ActionType.LAME_EXCUSE, "card40.pdf"),
    NOT_RESP("I'm not Responsible!", 4, ActionType.LAME_EXCUSE, "card41.pdf"),
    NULLPOINTER("Nullpointer!", 4, ActionType.BUG, "card33.pdf"),
    OFF_BY_ONE("Off By One!", 4, ActionType.BUG, "card34.pdf"),
    NOT_FOUND("Class Not Found!", 4, ActionType.BUG, "card35.pdf"),
    SYS_HANGS("System Hangs!", 4, ActionType.BUG, "card36.pdf"),
    CORE_DUMP("Core Dump!", 4, ActionType.BUG, "card37.pdf"),
    HATES_UI("Customer hates UI!", 4, ActionType.BUG, "card38.pdf");

    public final String label;
    public final int count;
    public final ActionType actionType;
    public final String fileName;

    private Action(String label, int count, ActionType actionType, String fileName) {
      this.label = label;
      this.count = count;
      this.actionType = actionType;
      this.fileName = fileName;
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

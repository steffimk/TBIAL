package de.lmu.ifi.sosy.tbial.game;

/** Action Cards are modeled with this class. */
public class ActionCard extends Card implements StackCard {

  private static final long serialVersionUID = 1L;

  private final Action action;

  public ActionCard(Action action) {
    super(CardType.ACTION);
    this.action = action;
  }

  public Action getAction() {
    return action;
  }

  public boolean isBug() {
    // TODO: Do some special cards count as bugs?
    return action.actionType == ActionType.BUG;
  }

  public ActionType getActionType() {
    return action.actionType;
  }

  @Override
  public String getResourceFileName() {
    return action.fileName;
  }

  @Override
  public String toString() {
    return action.toString();
  }

  /** Enum containing information about the specific action cards. */
  public enum Action {
    RED_BULL("Red Bull Dispenser", 1, ActionType.SPECIAL, "imgs/cards/card14.png"),
    HEISENBUG("Heisenbug", 1, ActionType.SPECIAL, "imgs/cards/card15.png"),
    LAN("LAN Party", 1, ActionType.SPECIAL, "imgs/cards/card16.png"),
    COFFEE("Coffee", 2, ActionType.SOLUTION, "imgs/cards/card22.png"),
    CODE_FIX("Code+Fix Session", 2, ActionType.SOLUTION, "imgs/cards/card23.png"),
    REGEX("I know regular expressions", 2, ActionType.SOLUTION, "imgs/cards/card24.png"),
    STAND_UP("Standup Meeting", 2, ActionType.SPECIAL, "imgs/cards/card25.png"),
    COFFEE_MACHINE("Presonal Coffee Machine", 2, ActionType.SPECIAL, "imgs/cards/card26.png"),
    BORING("Boring Meeting", 2, ActionType.SPECIAL, "imgs/cards/card30.png"),
    REFACTORED("I refactored your code. Away.", 4, ActionType.SPECIAL, "imgs/cards/card31.png"),
    PWND("Pwnd", 4, ActionType.SPECIAL, "imgs/cards/card32.png"),
    SYS_INT("System Integration", 3, ActionType.SPECIAL, "imgs/cards/card45.png"),
    WORKS("Works for me!", 4, ActionType.LAME_EXCUSE, "imgs/cards/card41.png"),
    FEATURE("It's a Feature", 4, ActionType.LAME_EXCUSE, "imgs/cards/card42.png"),
    NOT_RESP("I'm not Responsible!", 4, ActionType.LAME_EXCUSE, "imgs/cards/card43.png"),
    NULLPOINTER("Nullpointer!", 4, ActionType.BUG, "imgs/cards/card35.png"),
    OFF_BY_ONE("Off By One!", 4, ActionType.BUG, "imgs/cards/card36.png"),
    NOT_FOUND("Class Not Found!", 4, ActionType.BUG, "imgs/cards/card37.png"),
    SYS_HANGS("System Hangs!", 4, ActionType.BUG, "imgs/cards/card38.png"),
    CORE_DUMP("Core Dump!", 4, ActionType.BUG, "imgs/cards/card39.png"),
    HATES_UI("Customer hates UI!", 4, ActionType.BUG, "imgs/cards/card40.png");

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

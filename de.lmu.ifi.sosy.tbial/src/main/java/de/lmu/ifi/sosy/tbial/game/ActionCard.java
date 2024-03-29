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
    return action.actionType == ActionType.BUG;
  }

  public boolean isLameExcuse() {
    return action.actionType == ActionType.LAME_EXCUSE;
  }

  public boolean isSolution() {
    return action.actionType == ActionType.SOLUTION;
  }

  public boolean isSpecial() {
    return action.actionType == ActionType.SPECIAL;
  }

  public ActionType getActionType() {
    return action.actionType;
  }

  public boolean isImplemented() {
    return action.isImplemented;
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
    RED_BULL("Red Bull Dispenser", 1, ActionType.SPECIAL, "imgs/cards/card14.png", true),
    HEISENBUG("Heisenbug", 1, ActionType.SPECIAL, "imgs/cards/card15.png", false),
    LAN("LAN Party", 1, ActionType.SPECIAL, "imgs/cards/card16.png", true),
    COFFEE("Coffee", 2, ActionType.SOLUTION, "imgs/cards/card22.png", true),
    CODE_FIX("Code+Fix Session", 2, ActionType.SOLUTION, "imgs/cards/card23.png", true),
    REGEX("I know regular expressions", 2, ActionType.SOLUTION, "imgs/cards/card24.png", true),
    STAND_UP("Standup Meeting", 2, ActionType.SPECIAL, "imgs/cards/card25.png", false),
    COFFEE_MACHINE("Personal Coffee Machine", 2, ActionType.SPECIAL, "imgs/cards/card26.png", true),
    BORING("Boring Meeting", 2, ActionType.SPECIAL, "imgs/cards/card30.png", false),
    REFACTORED(
        "I refactored your code. Away.", 4, ActionType.SPECIAL, "imgs/cards/card31.png", false),
    PWND("Pwnd", 4, ActionType.SPECIAL, "imgs/cards/card32.png", false),
    SYS_INT("System Integration", 3, ActionType.SPECIAL, "imgs/cards/card45.png", false),
    WORKS("Works for me!", 4, ActionType.LAME_EXCUSE, "imgs/cards/card41.png", true),
    FEATURE("It's a Feature", 4, ActionType.LAME_EXCUSE, "imgs/cards/card42.png", true),
    NOT_RESP("I'm not Responsible!", 4, ActionType.LAME_EXCUSE, "imgs/cards/card43.png", true),
    NULLPOINTER("Nullpointer!", 4, ActionType.BUG, "imgs/cards/card35.png", true),
    OFF_BY_ONE("Off By One!", 4, ActionType.BUG, "imgs/cards/card36.png", true),
    NOT_FOUND("Class Not Found!", 4, ActionType.BUG, "imgs/cards/card37.png", true),
    SYS_HANGS("System Hangs!", 4, ActionType.BUG, "imgs/cards/card38.png", true),
    CORE_DUMP("Core Dump!", 4, ActionType.BUG, "imgs/cards/card39.png", true),
    HATES_UI("Customer hates UI!", 4, ActionType.BUG, "imgs/cards/card40.png", true);

    public final String label;
    public final int count;
    public final ActionType actionType;
    public final String fileName;
    public final boolean isImplemented;

    private Action(
        String label, int count, ActionType actionType, String fileName, boolean isImplemented) {
      this.label = label;
      this.count = count;
      this.actionType = actionType;
      this.fileName = fileName;
      this.isImplemented = isImplemented;
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

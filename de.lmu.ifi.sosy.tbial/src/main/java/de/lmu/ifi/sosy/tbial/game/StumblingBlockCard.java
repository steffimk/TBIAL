package de.lmu.ifi.sosy.tbial.game;

/** Stumbling Block Cards are modeled with this class. */
public class StumblingBlockCard extends Card implements StackCard {

  private final StumblingBlock stumblingBlock;

  public StumblingBlockCard(StumblingBlock stumblingBlock) {
    super(CardType.STUMBLING_BLOCK);
    this.stumblingBlock = stumblingBlock;
  }

  public StumblingBlock getStumblingBlock() {
    return stumblingBlock;
  }

  /** Enum containing information about the specific stumbling block cards. */
  public enum StumblingBlock {
    MAINTENANCE("Fortran Maintenance", 1),
    TRAINING("Off-The-Job Training", 3);

    public final String label;
    public final int count;

    private StumblingBlock(String label, int count) {
      this.label = label;
      this.count = count;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }
}

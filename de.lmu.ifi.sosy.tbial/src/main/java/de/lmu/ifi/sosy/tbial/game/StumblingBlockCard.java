package de.lmu.ifi.sosy.tbial.game;

/** Stumbling Block Cards are modeled with this class. */
public class StumblingBlockCard extends Card implements StackCard {

  private static final long serialVersionUID = 1L;

  private final StumblingBlock stumblingBlock;

  public StumblingBlockCard(StumblingBlock stumblingBlock) {
    super(CardType.STUMBLING_BLOCK);
    this.stumblingBlock = stumblingBlock;
  }

  public StumblingBlock getStumblingBlock() {
    return stumblingBlock;
  }

  @Override
  public boolean isBug() {
    return false; // TODO: Are StumblingBlockCards bugs?
  }

  @Override
  public String getResourceFileName() {
    return stumblingBlock.fileName;
  }

  @Override
  public String toString() {
    return stumblingBlock.toString();
  }

  /** Enum containing information about the specific stumbling block cards. */
  public enum StumblingBlock {
    MAINTENANCE("Fortran Maintenance", 1, "imgs/cards/card19.png"),
    TRAINING("Off-The-Job Training", 3, "imgs/cards/card46.png");

    public final String label;
    public final int count;
    public final String fileName;

    private StumblingBlock(String label, int count, String fileName) {
      this.label = label;
      this.count = count;
      this.fileName = fileName;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

}

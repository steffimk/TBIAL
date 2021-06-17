package de.lmu.ifi.sosy.tbial.game;

/** Ability Cards are modeled with this class. */
public class AbilityCard extends Card implements StackCard {

  private static final long serialVersionUID = 1L;

  private final Ability ability;

  public AbilityCard(Ability ability) {
    super(CardType.ABILITY);
    this.ability = ability;
  }

  public Ability getAbility(){
    return ability;
  }

  @Override
  public String getResourceFileName() {
    return ability.fileName;
  }

  @Override
  public boolean isBug() {
    return false;
  }

  @Override
  public String toString() {
    return ability.toString();
  }

  /** Enum containing information about the specific ability cards. */
  public enum Ability {
    SUNGLASSES("Wears Sunglasses at Work", 1, AbilityType.GARMENT, "" + "imgs/cards/card17.png"),
    TIE("Wears Tie at Work", 2, AbilityType.GARMENT, "imgs/cards/card27.png"),
    NASA("NASA", 1, AbilityType.PREVIOUS_JOB, "imgs/cards/card18.png"),
    ACCENTURE("ACCENTURE", 2, AbilityType.PREVIOUS_JOB, "imgs/cards/card28.png"),
    GOOGLE("Google", 2, AbilityType.PREVIOUS_JOB, "imgs/cards/card29.png"),
    MICROSOFT("Microsoft", 3, AbilityType.PREVIOUS_JOB, "imgs/cards/card47.png"),
    BUG_DELEGATION("Bug Delegation", 2, AbilityType.OTHER, "imgs/cards/card34.png");

    public final String label;
    public final int count;
    public final AbilityType abilityType;
    public final String fileName;

    private Ability(String label, int count, AbilityType abilityType, String fileName) {
      this.label = label;
      this.count = count;
      this.abilityType = abilityType;
      this.fileName = fileName;
    }
    
    @Override
    public String toString() {
      return this.label;
    }
  }

  public enum AbilityType {
    PREVIOUS_JOB,
    GARMENT,
    OTHER;
  }

  @Override
  public boolean isLameExcuse() {
    return false;
  }
}

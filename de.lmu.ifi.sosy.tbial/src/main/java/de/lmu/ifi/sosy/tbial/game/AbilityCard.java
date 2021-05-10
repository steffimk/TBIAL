package de.lmu.ifi.sosy.tbial.game;

/** Ability Cards are modeled with this class. */
public class AbilityCard extends Card implements StackCard {

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

  /** Enum containing information about the specific ability cards. */
  public enum Ability {
    SUNGLASSES("Wears Sunglasses at Work", 1, AbilityType.GARMENT, "card17.pdf"),
    TIE("Wears Tie at Work", 2, AbilityType.GARMENT, "card27.pdf"),
    NASA("NASA", 1, AbilityType.PREVIOUS_JOB, "card18.pdf"),
    ACCENTURE("ACCENTURE", 2, AbilityType.PREVIOUS_JOB, "card28.pdf"),
    GOOGLE("Google", 2, AbilityType.PREVIOUS_JOB, "card29.pdf"),
    MICROSOFT("Microsoft", 3, AbilityType.PREVIOUS_JOB, "card47.pdf"),
    BUG_DELEGATION("Bug Delegation", 2, AbilityType.OTHER, "card34.pdf");

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
}

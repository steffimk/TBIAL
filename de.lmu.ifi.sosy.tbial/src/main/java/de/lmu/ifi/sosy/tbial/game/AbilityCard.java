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

  /** Enum containing information about the specific ability cards. */
  public enum Ability {
    SUNGLASSES("Wears Sunglasses at Work", 1, AbilityType.GARMENT),
    TIE("Wears Tie at Work", 2, AbilityType.GARMENT),
    NASA("NASA", 1, AbilityType.PREVIOUS_JOB),
    ACCENTURE("ACCENTURE", 2, AbilityType.PREVIOUS_JOB),
    GOOGLE("Google", 2, AbilityType.PREVIOUS_JOB),
    MICROSOFT("Microsoft", 3, AbilityType.PREVIOUS_JOB),
    BUG_DELEGATION("Bug Delegation", 2, AbilityType.OTHER);

    public final String label;
    public final int count;
    public final AbilityType abilityType;

    private Ability(String label, int count, AbilityType abilityType) {
      this.label = label;
      this.count = count;
      this.abilityType = abilityType;
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

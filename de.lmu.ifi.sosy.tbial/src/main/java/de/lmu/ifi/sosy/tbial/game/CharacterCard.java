package de.lmu.ifi.sosy.tbial.game;

public class CharacterCard extends Card {

  private final Character character;

  public CharacterCard(Character character) {
    super(CardType.CHARACTER);
    this.character = character;
  }

  public Character getCharacter() {
    return this.character;
  }

  public enum Character {
    ZUCKERBERG("Mark Zuckerberg", 3),
    ANDERSON("Tom Anderson", 4),
    TAYLOR("Jeff Taylor", 4),
    PAGE("Larry Page", 4),
    ELLISON("Larry Ellison", 4),
    BECK("Kent Beck", 4),
    JOBS("Steve Jobs", 4),
    BALLMER("Steve Ballmer", 4),
    TORVALDS("Linus Torvalds", 4),
    THOU("Holier than Thou", 4),
    ZUSE("Konrad Zuse", 3),
    SCHNEIER("Bruce Schneier", 4),
    WEISSMAN("Terry Weissman", 4);

    public final String name;
    public final int maxHealthPoints;

    private Character(String name, int maxHealthPoints) {
      this.name = name;
      this.maxHealthPoints = maxHealthPoints;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }
	
}

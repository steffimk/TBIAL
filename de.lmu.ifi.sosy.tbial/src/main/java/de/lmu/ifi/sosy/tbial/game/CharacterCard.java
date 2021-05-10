package de.lmu.ifi.sosy.tbial.game;

/** Character Cards are modeled with this class. */
public class CharacterCard extends Card {

  private final Character character;

  public CharacterCard(Character character) {
    super(CardType.CHARACTER);
    this.character = character;
  }

  public Character getCharacter() {
    return this.character;
  }

  public int getMaxHealthPoints() {
    return character.maxHealthPoints;
  }

  @Override
  public String getResourceFileName() {
    return character.fileName;
  }

  /** Enum containing information about the specific character cards. */
  public enum Character {
    ZUCKERBERG("Mark Zuckerberg", 3, "card1.pdf"),
    ANDERSON("Tom Anderson", 4, "card2.pdf"),
    TAYLOR("Jeff Taylor", 4, "card3.pdf"),
    PAGE("Larry Page", 4, "card4.pdf"),
    ELLISON("Larry Ellison", 4, "card5.pdf"),
    BECK("Kent Beck", 4, "card6.pdf"),
    JOBS("Steve Jobs", 4, "card7.pdf"),
    BALLMER("Steve Ballmer", 4, "card8.pdf"),
    TORVALDS("Linus Torvalds", 4, "card9.pdf"),
    THOU("Holier than Thou", 4, "card10.pdf"),
    ZUSE("Konrad Zuse", 3, "card11.pdf"),
    SCHNEIER("Bruce Schneier", 4, "card12.pdf"),
    WEISSMAN("Terry Weissman", 4, "card13.pdf");

    public final String name;
    public final int maxHealthPoints;
    public final String fileName;

    private Character(String name, int maxHealthPoints, String fileName) {
      this.name = name;
      this.maxHealthPoints = maxHealthPoints;
      this.fileName = fileName;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }
	
}

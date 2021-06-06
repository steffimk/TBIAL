package de.lmu.ifi.sosy.tbial.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Character Cards are modeled with this class. */
public class CharacterCard extends Card {

  private static final long serialVersionUID = 1L;

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

  @Override
  public String toString() {
    return character.toString();
  }

  /**
   * Returns a list of count random character cards
   *
   * @param count - the amount of character cards to be returned
   * @return A list containing count random character cards
   */
  public static List<CharacterCard> getCharacterCards(int count) {
    ArrayList<CharacterCard> cards = new ArrayList<>();
    for (Character characater : Character.values()) {
      cards.add(new CharacterCard(characater));
    }
    Collections.shuffle(cards);
    return cards.subList(0, count);
  }

  /** Enum containing information about the specific character cards. */
  public enum Character {
    ZUCKERBERG("Mark Zuckerberg", 3, "imgs/cards/card1.png"),
    ANDERSON("Tom Anderson", 4, "imgs/cards/card2.png"),
    TAYLOR("Jeff Taylor", 4, "imgs/cards/card3.png"),
    PAGE("Larry Page", 4, "imgs/cards/card4.png"),
    ELLISON("Larry Ellison", 4, "imgs/cards/card5.png"),
    BECK("Kent Beck", 4, "imgs/cards/card6.png"),
    JOBS("Steve Jobs", 4, "imgs/cards/card7.png"),
    BALLMER("Steve Ballmer", 4, "imgs/cards/card8.png"),
    TORVALDS("Linus Torvalds", 4, "imgs/cards/card9.png"),
    THOU("Holier than Thou", 4, "imgs/cards/card10.png"),
    ZUSE("Konrad Zuse", 3, "imgs/cards/card11.png"),
    SCHNEIER("Bruce Schneier", 4, "imgs/cards/card12.png"),
    WEISSMAN("Terry Weissman", 4, "imgs/cards/card13.png");

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

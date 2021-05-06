package de.lmu.ifi.sosy.tbial.game;

public abstract class Card {

  private final CardType cardType;

  public Card(CardType cardType) {
    this.cardType = cardType;
  }

  public CardType getCardType() {
    return this.cardType;
  }

  public enum CardType {
    ROLE("Role Card", Color.GREEN),
    CHARACTER("Character Card", Color.YELLOW),
    ACTION("Action Card", Color.BLACK),
    ABILITY("Ability Card", Color.BLUE),
    STUMBLING_BLOCK("Stumbling Block Card", Color.MAGENTA);

    public final String label;
    public Color color;

    private CardType(String label, Color color) {
      this.label = label;
      this.color = color;
    }

    @Override
    public String toString() {
      return this.label;
    }

    public enum Color {
      GREEN("#33cc33"),
      YELLOW("#ffbf00"),
      BLACK("#000000"),
      BLUE("#00aaff"),
      MAGENTA("#e600ac");

      public final String hexColor;

      private Color(String hexColor) {
        this.hexColor = hexColor;
      }
    }
  }
}

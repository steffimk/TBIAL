package de.lmu.ifi.sosy.tbial.game;

/** Role Cards are modeled with this class. */
public class RoleCard extends Card {

  private final Role role;

  public RoleCard(Role role) {
    super(CardType.ROLE);
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  @Override
  public String getResourceFileName() {
    return role.fileName;
  }

  /** Enum containing information about the specific role cards. */
  public enum Role {
    MANAGER("Manager", "card20.pdf"),
    HONEST_DEVELOPER("Honest Developer", "card33.pdf"),
    EVIL_CODE_MONKEY("Evil Code Monkey", "card44.pdf"),
    CONSULTANT("Consultant", "card21.pdf");

    private final String label;
    public final String fileName;

    private Role(String label, String fileName) {
      this.label = label;
      this.fileName = fileName;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }
}

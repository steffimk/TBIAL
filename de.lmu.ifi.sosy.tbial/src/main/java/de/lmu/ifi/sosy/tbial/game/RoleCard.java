package de.lmu.ifi.sosy.tbial.game;

public class RoleCard extends Card {

  private final Role role;

  public RoleCard(Role role) {
    super(CardType.ROLE);
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public enum Role {
    MANAGER("Manager"),
    HONEST_DEVELOPER("Honest Developer"),
    EVIL_CODE_MONKEY("Evil Code Monkey"),
    CONSULTANT("Consultant");

    private final String label;

    private Role(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }
}

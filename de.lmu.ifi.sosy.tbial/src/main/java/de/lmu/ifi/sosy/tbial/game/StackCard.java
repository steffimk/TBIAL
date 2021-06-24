package de.lmu.ifi.sosy.tbial.game;

/** All cards that are part of the stack must implement this interface. */
public interface StackCard {
  // public boolean playCard();
  public abstract String getResourceFileName();

  public abstract boolean isBug();

  public abstract boolean isLameExcuse();
}

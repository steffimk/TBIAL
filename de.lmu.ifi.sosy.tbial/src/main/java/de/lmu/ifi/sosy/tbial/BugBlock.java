package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class BugBlock implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;

  public BugBlock(String sender) {
    super();
    this.sender = sender;
  }

  public String getSender() {
    return sender;
  }

  public String getTextMessage() {
    return sender + " played a bug against you!";
  }
}

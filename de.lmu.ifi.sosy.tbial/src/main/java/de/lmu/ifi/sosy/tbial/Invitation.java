package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class Invitation implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;
  private String textMessage;

  public Invitation(String sender) {
    super();
    this.sender = sender;
    this.textMessage = "has invited you to join his/her game.";
  }

  public String getSender() {
    return sender + " ";
  }

  public String getTextMessage() {
    return textMessage;
  }

}
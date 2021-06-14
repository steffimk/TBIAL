package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class Invitation implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;
  private String textMessage;

  public Invitation(String sender, String textMessage) {
    super();
    this.sender = sender;
    this.textMessage = textMessage;
  }

  public String getSender() {
    return sender;
  }

  public String getTextMessage() {
    return " " + textMessage;
  }

}
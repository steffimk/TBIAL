package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class Invitation implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;
  private String textMessage;

  private String gameName;

  public Invitation(String sender, String textMessage, String gameName) {
    super();
    this.sender = sender;
    this.textMessage = textMessage;
    this.gameName = gameName;
  }

  public String getSender() {
    return sender;
  }

  public String getTextMessage() {
    return " " + textMessage;
  }

  public String getGameName() {
    return gameName;
  }
}
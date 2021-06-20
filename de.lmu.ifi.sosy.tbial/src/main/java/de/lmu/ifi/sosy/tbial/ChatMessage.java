package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class ChatMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;
  private String textMessage;

  public ChatMessage(String sender, String textMessage) {
    super();
    this.sender = sender;
    this.textMessage = textMessage;
  }

  /**
   * Use this constructor to send game updates
   *
   * @param textMessage
   */
  public ChatMessage(String textMessage) {
    super();
    this.sender = "GAME UPDATE";
    this.textMessage = textMessage;
  }

  public String getSender() {
    return sender + ": ";
  }

  public String getTextMessage() {
    return textMessage;
  }

  public boolean isMessageEmpty() {
    return textMessage == null;
  }
}
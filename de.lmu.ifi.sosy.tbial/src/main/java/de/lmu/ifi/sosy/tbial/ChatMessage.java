package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;

public class ChatMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  private String sender;
  private String receiver;
  private String textMessage;

  private boolean personal;

  public ChatMessage(String sender, String textMessage, boolean personal, String receiver) {
    super();
    this.sender = sender;
    this.textMessage = textMessage;
    this.personal = personal;
    this.receiver = receiver;
  }

  /**
   * Use this constructor to send game updates
   *
   * @param textMessage
   */
  public ChatMessage(String textMessage, boolean personal, String receiver) {
    super();
    this.sender = "UPDATE";
    this.textMessage = textMessage;
    this.personal = personal;
    this.receiver = receiver;
  }

  public String getSender() {
    return sender + ": ";
  }

  public String getPureSender() {
    return sender;
  }

  public String getTextMessage() {
    return textMessage;
  }

  public boolean isMessageEmpty() {
    return textMessage == null;
  }

  public boolean isPersonal() {
    return personal;
  }

  public String getReceiver() {
    return receiver;
  }
}
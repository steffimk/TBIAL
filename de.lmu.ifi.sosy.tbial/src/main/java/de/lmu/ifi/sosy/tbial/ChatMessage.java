package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  private String sender;
  private String receiver;
  private String textMessage;
  private boolean isGameUpdate;
  private String timestamp;

  private boolean personal;

  public ChatMessage(String sender, String textMessage, boolean personal, String receiver) {
    super();
    this.sender = sender;
    this.textMessage = textMessage;
    this.isGameUpdate = false;
    this.timestamp = LocalDateTime.now().format(timeFormatter);
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
    this.isGameUpdate = true;
    this.timestamp = LocalDateTime.now().format(timeFormatter);
    this.personal = personal;
    this.receiver = receiver;
  }

  public String getSender() {
    if (isGameUpdate) return "";
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

  public String getTimestamp() {
    return this.timestamp;
  }

  public boolean isGameUpdate() {
    return this.isGameUpdate;
  }

  public boolean isPersonal() {
    return personal;
  }

  public String getReceiver() {
    return receiver;
  }
}
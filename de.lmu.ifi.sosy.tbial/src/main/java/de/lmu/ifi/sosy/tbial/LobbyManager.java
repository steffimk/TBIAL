package de.lmu.ifi.sosy.tbial;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.lmu.ifi.sosy.tbial.db.User;

public class LobbyManager {
  private Map<String, User> users;
  private LinkedList<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

  public static LobbyManager instance = new LobbyManager();

  public LobbyManager() {
    users = Collections.synchronizedMap(new HashMap<>());
  }

  public void addUser(User user) {
    instance.users.put(user.getName(), user);
  }

  public void clear() {
    users.clear();
  }

  public LinkedList<ChatMessage> getChatMessages() {
    return chatMessages;
  }
}

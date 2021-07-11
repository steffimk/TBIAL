package de.lmu.ifi.sosy.tbial;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

public class ChatPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final int maxMessages = 80;

  private MarkupContainer chatMessagesContainer;

  public ChatPanel(String id, TBIALSession session) {
    super(id);

    final TextField<String> textField = new TextField<String>("message", new Model<String>());
    textField.setOutputMarkupId(true);

    chatMessagesContainer = new WebMarkupContainer("chatMessages");

    IModel<List<ChatMessage>> chatMessageModel =
        (IModel<List<ChatMessage>>) () -> session.getGame().getChatMessages();

    final PropertyListView<ChatMessage> listView =
        new PropertyListView<ChatMessage>("messages", chatMessageModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<ChatMessage> item) {
            this.modelChanging();

            ChatMessage chatMessage = item.getModelObject();
            User user = session.getUser();

            Label time = new Label("time", chatMessage.getTimestamp());
            item.add(time);

            // public messages
            if (!chatMessage.isPersonal()) {
              Label sender = new Label("sender", new PropertyModel<String>(chatMessage, "sender"));
              item.add(sender);
              Label text =
                  new Label("textMessage", new PropertyModel<String>(chatMessage, "textMessage"));
              item.add(text);

              // public update messages
              if (chatMessage.isGameUpdate()) {
                text.add(new AttributeModifier("style", "font-style: italic;"));
              }
            }
            // private messages
            else if (chatMessage.isPersonal()
                && (chatMessage.getPureSender().equals(user.getName())
                    || chatMessage.getReceiver().equals(user.getName()))) {
              Label sender;
              Label text =
                  new Label("textMessage", new PropertyModel<String>(chatMessage, "textMessage"));

              // private game updates
              if (chatMessage.isGameUpdate()) {
                sender = new Label("sender", new PropertyModel<String>(chatMessage, "sender"));
                text.add(new AttributeModifier("style", "font-style: italic;"));
              }
              // private user messages
              else {
                sender =
                    new Label(
                        "sender",
                        () ->
                            chatMessage.getPureSender()
                                + " to "
                                + chatMessage.getReceiver()
                                + ": ");
                sender.add(new AttributeModifier("style", "color: #F4731D;"));
                text.add(new AttributeModifier("style", "color: #F4731D;"));
              }
              item.add(sender);
              item.add(text);
            }
            // hide private messages from not involved users
            else {
              item.setVisible(false);
            }
          }
        };

    chatMessagesContainer.setOutputMarkupId(true);
    chatMessagesContainer.add(listView);

    AjaxSelfUpdatingTimerBehavior ajaxBehavior =
        new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3));
    chatMessagesContainer.add(ajaxBehavior);
    add(chatMessagesContainer);

    AjaxButton send =
        new AjaxButton("send") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onSubmit(AjaxRequestTarget target) {
            String username = ((TBIALSession) getSession()).getUser().getName();
            String text = textField.getModelObject();

            ChatMessage chatMessage = null;
            Game game = session.getGame();
            User user = session.getUser();

            if (text != null) {
              // whisper private message to other user
              if (text.contains("/w")) {
                if (game.getAllInGamePlayerNames().stream().anyMatch(text::contains)) {
                  String receiver =
                      game.getAllInGamePlayerNames()
                          .stream()
                          .filter(text::contains)
                          .findAny()
                          .get();
                  String[] parts = text.split(" ");
                  String textMessage = "";
                  for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals(receiver)) {
                      for (int j = i + 1; j < parts.length; j++) {
                        textMessage += parts[j] + " ";
                      }
                      break;
                    }
                  }
                  chatMessage = new ChatMessage(username, textMessage, true, receiver);
                } else return;

              }

              // reply to private message
              else if (text.contains("/r")) {
                String textMessage = text.substring(3);
                String sender = game.getSenderOfLastPersonalMessageToMe(user.getName());
                System.out.println(sender);
                if (sender != null) {
                  chatMessage = new ChatMessage(username, textMessage, true, sender);
                } else return;
              }

              // all other messages
              else {
                chatMessage = new ChatMessage(username, text, false, "all");
              }
            }

            if (chatMessage != null) {
              if (chatMessage.isMessageEmpty()) return;
              LinkedList<ChatMessage> chatMessages = game.getChatMessages();
              synchronized (chatMessages) {
                if (chatMessages.size() >= maxMessages) {
                  chatMessages.removeFirst();
                }

                chatMessages.addFirst(chatMessage);
              }
            }

            textField.setModelObject("");
            target.add(chatMessagesContainer, textField);
          }
        };

    Component chatForm = new Form<String>("form").add(textField, send);
    add(chatForm);
  }
}

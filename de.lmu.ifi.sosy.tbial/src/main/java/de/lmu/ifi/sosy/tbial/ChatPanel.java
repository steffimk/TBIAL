package de.lmu.ifi.sosy.tbial;

import java.util.LinkedList;

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
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

public class ChatPanel extends Panel {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private static final int maxMessages = 80;

  private MarkupContainer chatMessagesContainer;

  public ChatPanel(String id, LinkedList<ChatMessage> chatMessages) {
    super(id);

    final TextField<String> textField = new TextField<String>("message", new Model<String>());
    textField.setOutputMarkupId(true);

    chatMessagesContainer = new WebMarkupContainer("chatMessages");

    final ListView<ChatMessage> listView =
        new ListView<ChatMessage>("messages", chatMessages) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<ChatMessage> item) {
            this.modelChanging();

            ChatMessage chatMessage = item.getModelObject();

            Label sender = new Label("sender", new PropertyModel<String>(chatMessage, "sender"));
            item.add(sender);

            Label text =
                new Label("textMessage", new PropertyModel<String>(chatMessage, "textMessage"));
            item.add(text);
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

            ChatMessage chatMessage = new ChatMessage(username, text);

            if (chatMessage.isMessageEmpty()) return;

            synchronized (chatMessages) {
              if (chatMessages.size() >= maxMessages) {
                chatMessages.removeFirst();
              }

              chatMessages.addLast(chatMessage);
            }

            textField.setModelObject("");
            target.add(chatMessagesContainer, textField);
          }
        };

    Component chatForm = new Form<String>("form").add(textField, send);
    add(chatForm);
  }
}

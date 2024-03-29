package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;

import de.lmu.ifi.sosy.tbial.game.Turn.TurnStage;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.Turn;

public class BugBlockPanel extends Panel {
  static final long serialVersionUID = 1L;

  BugBlockPanel(String id, TBIALSession session, Player player) {
	  super(id);

    IModel<List<BugBlock>> bugBlockModel =
        (IModel<List<BugBlock>>) () -> new ArrayList<BugBlock>(player.getBugBlocks());
    ListView<BugBlock> bugBlocks =
        new PropertyListView<>("bugBlockContainer", bugBlockModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<BugBlock> item) {
            WebMarkupContainer bugBlockForm = new WebMarkupContainer("bugBlockForm");
            Form<?> block = new Form<>("block bug");
            Form<?> reject = new Form<>("do nothing");

            final BugBlock bugBlock = item.getModelObject();
            bugBlockForm.add(
                new Label(
                    "bugBlockSenderMessage",
                    bugBlock.getSender() + " " + bugBlock.getTextMessage()));

            Button blockButton =
                new Button("blockButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    session.getGame().getTurn().setStage(TurnStage.CHOOSING_CARD_TO_BLOCK_WITH);
                  }
                };
            Button rejectButton =
                new Button("rejectButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    player.getBugBlocks().remove(bugBlock);
                    remove(bugBlockForm);

                    Game currentGame = session.getGame();

                    currentGame.putCardOnHeap(player, currentGame.getTurn().getLastPlayedBugCard());
                    player.getReceivedCards().remove(currentGame.getTurn().getLastPlayedBugCard());

                    player.clearBugBlocks();
                    currentGame.getTurn().setAttackedPlayer(null);
                    currentGame.getTurn().setLastPlayedBugCard(null);
                    currentGame.getTurn().setLastPlayedBugCardBy(null);

                    currentGame
                        .getChatMessages()
                        .addFirst(
                            new ChatMessage(
                                player.getUserName() + " rejected to block Bug", false, "all"));

                    currentGame.getTurn().setStage(Turn.TurnStage.PLAYING_CARDS);
                  }
                };

            block.add(blockButton);
            reject.add(rejectButton);

            bugBlockForm.add(block);
            bugBlockForm.add(reject);
            item.add(bugBlockForm);
          }
        };

    add(bugBlocks);
  }
}

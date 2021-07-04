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

  BugBlockPanel(String id, Game currentGame, Player player) {
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
            bugBlockForm.add(new Label("bugBlockSender", bugBlock.getSender()));
            bugBlockForm.add(new Label("bugBlockMessage", bugBlock.getTextMessage()));
            currentGame.getTurn().setStage(Turn.TurnStage.WAITING_FOR_PLAYER_RESPONSE);

            Button blockButton =
                new Button("blockButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    currentGame.getTurn().setStage(TurnStage.CHOOSING_CARD_TO_BLOCK_WITH);
                  }
                };
            Button rejectButton =
                new Button("rejectButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    player.getBugBlocks().remove(bugBlock);
                    remove(bugBlockForm);

                    currentGame.putCardOnHeap(player, currentGame.getTurn().getLastPlayedBugCard());
                    player.getReceivedCards().remove(currentGame.getTurn().getLastPlayedBugCard());

                    currentGame.getTurn().setAttackedPlayer(null);

                    //
                    currentGame
                        .getChatMessages()
                        .add(new ChatMessage(player.getUserName() + " rejected to block Bug"));

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

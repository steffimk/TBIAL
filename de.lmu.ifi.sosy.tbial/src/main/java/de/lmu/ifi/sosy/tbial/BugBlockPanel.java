package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import org.apache.wicket.markup.html.list.PropertyListView;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.ActionCard;
import de.lmu.ifi.sosy.tbial.game.Card;
import de.lmu.ifi.sosy.tbial.game.Card.CardType;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.GameManager;
import de.lmu.ifi.sosy.tbial.game.Player;
import de.lmu.ifi.sosy.tbial.game.StackCard;
import de.lmu.ifi.sosy.tbial.game.Turn;

public class BugBlockPanel extends Panel {
  static final long serialVersionUID = 1L;

  protected GameManager getGameManager() {
    return getTbialApplication().getGameManager();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) super.getApplication();
  }

  BugBlockPanel(String id, Game currentGame, IModel<Player> player, Player basePlayer) {
	  super(id);


    IModel<List<BugBlock>> bugBlockModel =
        (IModel<List<BugBlock>>) () -> new ArrayList<BugBlock>(player.getObject().getBugBlocks());
    ListView<BugBlock> bugBlocks =
        new PropertyListView<>("bugBlockContainer", bugBlockModel) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<BugBlock> item) {
            WebMarkupContainer bugBlockForm = new WebMarkupContainer("bugBlockForm");
            Form<?> block = new Form<>("block");
            Form<?> reject = new Form<>("reject");

            final BugBlock bugBlock = item.getModelObject();
            bugBlockForm.add(new Label("bugBlockSender", bugBlock.getSender()));
            bugBlockForm.add(new Label("bugBlockMessage", bugBlock.getTextMessage()));
            currentGame.getTurn().setStage(Turn.TurnStage.WAITING_FOR_PLAYER_RESPONSE);

            Button blockButton =
                new Button("blockButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    for (StackCard card : player.getObject().getHandCards()) {
                      if (((Card) card).getCardType() == CardType.ACTION) {
                        if (((ActionCard) card).isLameExcuse()) {
                          ActionCard lameExcuseCard = (ActionCard) card;
                          currentGame.defendBugImmediately(player.getObject(), lameExcuseCard);
                        }
                      }
                    }

                    currentGame.getTurn().setStage(Turn.TurnStage.PLAYING_CARDS);
                  }
                };
            Button rejectButton =
                new Button("rejectButton") {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onSubmit() {
                    player.getObject().getBugBlocks().remove(bugBlock);
                    remove(bugBlockForm);

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

    add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(bugBlocks);
  }
}

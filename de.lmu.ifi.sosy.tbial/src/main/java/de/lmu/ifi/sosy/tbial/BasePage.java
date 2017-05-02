package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

/**
 * Basic page with style template as well as access to {@link TBIALSession} and {@link Database}.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public abstract class BasePage extends WebPage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private Link<Void> link;

  protected Database getDatabase() {
    return TBIALApplication.getDatabase();
  }

  @Override
  public TBIALSession getSession() {
    return (TBIALSession) super.getSession();
  }

  public BasePage() {
    link =
        new Link<Void>("signout") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            Session session = getSession();
            if (session instanceof AuthenticatedWebSession) {
              ((AuthenticatedWebSession) session).signOut();
            }
            session.invalidate();
          }
        };

    add(link);

    if (!getSession().isSignedIn()) {
      link.setVisible(false);
      link.setEnabled(false);
    }
  }
}

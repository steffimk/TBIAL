package de.lmu.ifi.sosy.tbial;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.lmu.ifi.sosy.tbial.db.User;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

/** Simple test using the WicketTester */
public class LoginTest extends PageTestBase {
  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
  }

  @Test
  public void loginPageHasEmptyForm() {
    tester.startPage(Login.class);
    tester.assertComponent("login:name", TextField.class);
    tester.assertComponent("login:password", PasswordTextField.class);
    tester.assertModelValue("login:name", "");
    tester.assertModelValue("login:password", null);
  }

  @Test
  public void loginOK() {
    attemptLogin("testuser", "testpassword");

    TBIALSession session = getSession();
    assertNotNull(session);
    assertTrue(session.isSignedIn());
    User user = session.getUser();
    assertNotNull(user);
    assertThat(user, hasNameAndPassword("testuser", "testpassword"));

    tester.assertRenderedPage(Lobby.class);
  }

  private void attemptLogin(String name, String password) {
    // start and render the test page
    tester.startPage(Login.class);

    // assert rendered page class
    tester.assertRenderedPage(Login.class);

    FormTester form = tester.newFormTester("login");
    form.setValue("name", name);
    form.setValue("password", password);
    form.submit("loginbutton");
  }

  @Test
  public void loginErrorUnknownUser() {
    attemptLogin("unknownuser", "testpassword");

    TBIALSession session = getSession();
    assertNotNull(session);
    assertFalse(session.isSignedIn());
    assertThat(session.getUser(), nullValue());

    tester.assertRenderedPage(Login.class);
  }

  @Test
  public void loginErrorWrongPassword() {
    attemptLogin("testuser", "wrongpassword");

    TBIALSession session = getSession();
    assertNotNull(session);
    assertFalse(session.isSignedIn());
    assertThat(session.getUser(), nullValue());

    tester.assertRenderedPage(Login.class);
  }

  @Test
  public void logoutInLobby() {
    attemptLogin("testuser", "testpassword");

    tester.assertRenderedPage(Lobby.class);

    tester.clickLink("signout");

    TBIALSession session = getSession();
    assertNotNull(session);
    assertFalse(session.isSignedIn());

    assertThat(session.getUser(), nullValue());

    tester.startPage(Lobby.class);

    // check redirection to login.
    tester.assertRenderedPage(Login.class);
  }
}

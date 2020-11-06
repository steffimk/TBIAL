package de.lmu.ifi.sosy.tbial;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

public class RegisterTest extends PageTestBase {

  @Before
  public void setUp() {
    setupApplication();
    database.register("testuser", "testpassword");
  }

  private void assertAllEmpty() {
    tester.assertModelValue("register:name", "");
    tester.assertModelValue("register:nameFeedback", " ");
    tester.assertModelValue("register:password", null);
    tester.assertModelValue("register:passwordConfirm", null);
  }

  @Test
  public void registerOk() {
    tester.startPage(Register.class);
    tester.assertRenderedPage(Register.class);
    assertAllEmpty();

    attemptRegister("user2", "password2", "password2");

    TBIALSession session = getSession();
    assertNotNull(session);
    assertTrue(session.isSignedIn());
    assertThat(session.getUser(), hasNameAndPassword("user2", "password2"));

    tester.assertRenderedPage(TBIALApplication.get().getHomePage());
  }

  private void attemptRegister(String user, String password, String passwordConfirm) {
    FormTester form = tester.newFormTester("register");
    form.setValue("name", user);
    form.setValue("password", password);
    form.setValue("passwordConfirm", passwordConfirm);

    form.submit("register");
  }

  @Test
  public void registerErrorWhenDuplicateUser() {
    tester.startPage(Register.class);
    tester.assertRenderedPage(Register.class);
    assertAllEmpty();

    attemptRegister("testuser", "testpassword", "testpassword");

    assertNotSignedIn();

    tester.assertRenderedPage(Register.class);

    String errorMsg = "A user with that name already exists. Please choose another name.";
    tester.assertErrorMessages(errorMsg);
    tester.assertFeedback("feedback", errorMsg);
  }

  private void assertNotSignedIn() {
    TBIALSession session = getSession();
    assertNotNull(session);
    assertFalse(session.isSignedIn());
    assertNull(session.getUser());
  }

  @Test
  public void registerErrorWhenWrongPasswordConfirmation() {
    tester.startPage(Register.class);
    tester.assertRenderedPage(Register.class);
    assertAllEmpty();

    attemptRegister("testuser", "testpassword", "bla");

    assertNotSignedIn();

    tester.assertRenderedPage(Register.class);

    String errorMsg = "Password and confirmation do not match. Please verify and try again.";
    tester.assertErrorMessages(errorMsg);
    tester.assertFeedback("feedback", errorMsg);
  }

  @Test
  public void registerFeedbackWhileTyping() {
    tester.startPage(Register.class);
    tester.assertRenderedPage(Register.class);
    assertAllEmpty();
    FormTester form = tester.newFormTester("register");
    form.setValue("name", "test");

    tester.executeAjaxEvent("register:name", "change");
    tester.assertComponentOnAjaxResponse("register:nameFeedback");

    System.out.println("bla");
    tester.assertModelValue("register:nameFeedback", " ");

    form.setValue("name", "testuser");
    tester.executeAjaxEvent("register:name", "change");
    tester.assertModelValue("register:nameFeedback", "Name already taken.");
  }
}

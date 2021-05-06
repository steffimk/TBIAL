package de.lmu.ifi.sosy.tbial.db;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractDatabaseTest {

  protected Database database;

  protected String name;

  protected String password;

  protected User user;

  @Before
  public void initGeneral() {
    name = "name";
    password = "pass";
    user = new User(name, password);
  }

  protected void addUser() {
    addUser(user);
  }

  protected abstract void addUser(User user);

  @Test(expected = NullPointerException.class)
  public void registerUserWhenNullNameGivenThrowsException() {
    database.register(null, password);
  }

  @Test(expected = NullPointerException.class)
  public void registerUserWhenNullPasswordGivenThrowsException() {
    database.register(name, null);
  }

  @Test
  public void hasUserWithNameWhenUserNotRegisteredReturnsFalse() {
    assertThat(database.userNameTaken(name), is(false));
  }

  @Test
  public void hasUserWithNameWhenUserRegisteredReturnsTrue() {
    addUser();
    assertThat(database.userNameTaken(name), is(true));
  }

  @Test
  public void registerUserWhenUserExistsReturnsNull() {
    addUser();
    User user = database.register(name, password);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void registerUserWhenUserDoesNotExistReturnsUser() {
    User user = database.register(name, password);

    assertThat(user, hasNameAndPassword(name, password));
  }

  @Test(expected = NullPointerException.class)
  public void getUserWhenNullGivenThrowsException() {
    database.getUser(null);
  }

  @Test
  public void getUserWhenUserDoesNotExistReturnsNull() {
    addUser(new User("someoneelse", "withsomepassword"));
    User user = database.getUser(name);

    assertThat(user, is(nullValue()));
  }

  @Test
  public void getUserWhenNoUserExistsReturnsNull() {
    User user = database.getUser(name);

    assertThat(user, is(nullValue()));
  }

  @Test
  public void getUserWhenUserExistsReturnsUser() {
    addUser();

    User user = database.getUser(name);

    assertThat(user, hasNameAndPassword(name, password));
  }

  @Test
  public void getUserWhenMultipleUsersExistsReturnsCorrectUser() {
    addUser();
    addUser(new User("AnotherName", "AnotherPass"));

    User user = database.getUser(name);

    assertThat(user, hasNameAndPassword(name, password));
  }
}

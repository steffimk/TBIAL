package de.lmu.ifi.sosy.tbial.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

  private String password;

  private String name;

  private User user;

  private int id;

  @Before
  public void init() {
    password = "pass";
    name = "name";
    id = 42;
    user = new User("", "");
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNameGiven_throwsException() {
    new User(null, password);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullPasswordGiven_throwsException() {
    new User(name, null);
  }

  @Test
  public void getPassword_returnsPassword() {
    user.setPassword(password);
    assertThat(user.getPassword(), is(password));
  }

  @Test
  public void getName_returnsName() {
    user.setName(name);
    assertThat(user.getName(), is(name));
  }

  @Test
  public void getId_returnsId() {
    user.setId(id);
    assertThat(user.getId(), is(id));
  }

  @Test(expected = NullPointerException.class)
  public void setPassword_whenNullPasswordGiven_throwsException() {
    user.setPassword(null);
  }

  @Test(expected = NullPointerException.class)
  public void setName_whenNullNameGiven_throwsException() {
    user.setName(null);
  }
}

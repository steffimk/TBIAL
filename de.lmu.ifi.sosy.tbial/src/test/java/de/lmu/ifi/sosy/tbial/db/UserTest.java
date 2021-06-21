package de.lmu.ifi.sosy.tbial.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.Invitation;

public class UserTest {

  private String password;

  private String name;

  private User user;

  private int id;

  private Invitation invitation;

  @Before
  public void init() {
    password = "pass";
    name = "name";
    id = 42;
    user = new User("", "");
    invitation = new Invitation("sender", "sending message", "game");
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

  @Test
  public void getInvitations_returnsInvitations() {
    user.invite(invitation);
    assertThat(user.getInvitations().get(0), is(invitation));
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

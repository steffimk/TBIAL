package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.User;
import java.util.Objects;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class TestUtil {

  public static Matcher<User> hasNameAndPassword(final String name, final String password) {
    return new BaseMatcher<User>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("has name '" + name + "' and password '" + password + "'");
      }

      @Override
      public boolean matches(Object item) {
        if (!(item instanceof User)) {
          return false;
        }
        User user = (User) item;
        return Objects.equals(user.getName(), name) && Objects.equals(user.getPassword(), password);
      }
    };
  }
}

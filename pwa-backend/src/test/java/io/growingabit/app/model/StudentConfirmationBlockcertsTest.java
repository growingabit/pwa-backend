package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Splitter;

public class StudentConfirmationBlockcertsTest {

  private StudentConfirmationBlockcerts s;
  private final String host = "http://localhost";
  private final String userId = "userId";

  @Before
  public void setup() {
    this.s = new StudentConfirmationBlockcerts(this.host, this.userId);
    System.out.println(this.s.getNonce());
  }


  @Test(expected = IllegalArgumentException.class)
  public void nullHost() {
    new StudentConfirmationBlockcerts(null, this.userId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHost() {
    new StudentConfirmationBlockcerts("", this.userId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullUser() {
    new StudentConfirmationBlockcerts(this.host, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyUser() {
    new StudentConfirmationBlockcerts(this.host, "");
  }

  @Test
  public void tsExpirationIsFuture() {
    assertThat(this.s.getTsExpiration()).isGreaterThan(new DateTime().getMillis());
  }

  @Test
  public void nonceIsNotNull() {
    assertThat(this.s.getNonce()).isNotNull();
  }

  @Test
  public void nonceIsNotEmpty() {
    assertThat(this.s.getNonce()).isNotEmpty();
  }

  @Test
  public void validNonce() {
    try {
      List<String> list = Splitter.on(":").splitToList(new String(Base64.decodeBase64(this.s.getNonce()), "utf-8"));
      String userId = list.get(0);
      String hash = list.get(1);

      String left = StringUtils.left(new String(DigestUtils.sha1(this.s.getUserId() + this.s.getTsExpiration() + this.s.getOrigin()), "utf-8"), 5);
      assertThat(hash).isEqualTo(left);
      assertThat(userId).isEqualTo(this.s.getUserId());

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

}

package io.growingabit.sms;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.Message.Status;

import io.growingabit.sms.exceptions.SMSSendingException;

public class SMSSenderTest {

  private static final String VALID_FROM = "+15005550006";
  private static final String INVALID_FROM = "+15005550001";
  private static final String FULL_QUEUE_FROM = "+15005550008";
  private static final String NOT_OWNED_FROM = "+15005550007";

  private static final String VALID_TO = "+393351234567";
  private static final String INVALID_TO = "+15005550001";
  private static final String CANNOT_ROUTE_TO = "+15005550002";
  private static final String NO_INTERNATIONAL_PERMISSION_TO = "+15005550003";
  private static final String BLACKLISTED_TO = "+15005550004";
  private static final String NO_SMS_TO = "+15005550009";

  @Test
  @Ignore
  public void sendSMS() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, VALID_TO, "a text");
    assertThat(message.getStatus()).isEqualTo(Status.QUEUED);
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void invalidFrom() {
    final Message message = new SMSSender().sendMessage(INVALID_FROM, VALID_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void fullQueueFrom() {
    final Message message = new SMSSender().sendMessage(FULL_QUEUE_FROM, VALID_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void notOwnedFrom() {
    final Message message = new SMSSender().sendMessage(NOT_OWNED_FROM, VALID_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void invalidTo() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, INVALID_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void cannotRouteTo() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, CANNOT_ROUTE_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void noInternationalPermissionTo() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, NO_INTERNATIONAL_PERMISSION_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void blacklistedTo() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, BLACKLISTED_TO, "a text");
  }

  @Test(expected = SMSSendingException.class)
  @Ignore
  public void noSmsTo() {
    final Message message = new SMSSender().sendMessage(VALID_FROM, NO_SMS_TO, "a text");
  }

}

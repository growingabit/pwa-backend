package io.growingabit.sms;

import org.apache.commons.configuration2.Configuration;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import io.growingabit.app.utils.Settings;
import io.growingabit.sms.exceptions.SMSSendingException;

public class SMSSender {

  public Message sendMessage(final String from, final String to, final String text) {
    try {
      final Configuration config = Settings.getConfig();

      final String accountSid = config.getString("twilio.accountSid");
      final String authToken = config.getString("twilio.accountToken");

      Twilio.init(accountSid, authToken);

      return Message.creator(new PhoneNumber(to), new PhoneNumber(from), text).create();
    } catch (final ApiException e) {
      throw new SMSSendingException(e);
    }
  }

}

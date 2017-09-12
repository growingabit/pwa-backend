package io.growingabit.mail;

import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;

public class MailJet {

  public static void sendEmail(MailService mailService) {

    // @formatter:off
    MailjetRequest email = new MailjetRequest(Emailv31.resource)
        .property(Emailv31.MESSAGES, new JSONArray()
          .put(new JSONObject()
            .put(Emailv31.Message.FROM, new JSONObject()
              .put("Email", sender)
              .put("Name", "pandora"))
            .put(Emailv31.Message.TO, new JSONArray()
              .put(new JSONObject()
                .put("Email", recipient)))
            .put(Emailv31.Message.SUBJECT, "Your email flight plan!")
            .put(Emailv31.Message.TEXTPART, "Dear passenger, welcome to Mailjet! May the delivery force be with you!")
            .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger, welcome to Mailjet!</h3><br />May the delivery force be with you!")));

    // @formatter:on

    try {
      // trigger the API call
      MailjetResponse response = client.post(email);
      // Read the response data and status
      resp.getWriter().print(response.getStatus());
      resp.getWriter().print(response.getData());
    } catch (MailjetException e) {
      throw new ServletException("Mailjet Exception", e);
    } catch (MailjetSocketTimeoutException e) {
      throw new ServletException("Mailjet socket timed out", e);
    }

  }

}

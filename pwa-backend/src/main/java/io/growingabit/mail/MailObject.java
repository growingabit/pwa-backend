package io.growingabit.mail;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.common.base.Preconditions;

public class MailObject {

  private Set<String> to;
  private Set<String> cc;
  private Set<String> bcc;
  private String subject;
  private String body;
  private BodyType bodyType;

  private MailObject(Set<String> to, Set<String> cc, Set<String> bcc, String subject, String body, BodyType bodyType) {
    this.to = to;
    this.cc = cc;
    this.bcc = bcc;
    this.subject = subject;
    this.body = body;
    this.bodyType = bodyType;
  }

  public Set<String> getTo() {
    return this.to;
  }

  public Set<String> getCc() {
    return this.cc;
  }

  public Set<String> getBcc() {
    return this.bcc;
  }

  public String getSubject() {
    return this.subject;
  }

  public String getBody() {
    return this.body;
  }

  public BodyType getBodyType() {
    return this.bodyType;
  }

  public static class Builder {

    private String subject;
    private String body;
    private BodyType bodyType;
    private Set<String> to;
    private Set<String> cc;
    private Set<String> bcc;

    public Builder(String to, String subject) {
      Preconditions.checkArgument(EmailValidator.getInstance().isValid(to));

      this.to = new LinkedHashSet<String>();
      this.cc = new LinkedHashSet<String>();
      this.bcc = new LinkedHashSet<String>();

      this.to.add(to);
      this.subject = subject;
    }

    public Builder withHtmlBody(String htmlBody) {
      this.body = htmlBody;
      this.bodyType = BodyType.HTML;
      return this;
    }

    public Builder withTextBody(String textBody) {
      this.body = textBody;
      this.bodyType = BodyType.TEXT;
      return this;
    }

    public Builder addTo(String address) {
      return this.addRecipient(address, this.to);
    }

    public Builder addTo(Collection<String> address) {
      return this.addRecipients(address, this.to);
    }

    public Builder addCc(String address) {
      return this.addRecipient(address, this.cc);
    }

    public Builder addCc(Collection<String> address) {
      return this.addRecipients(address, this.cc);
    }

    public Builder addBcc(String address) {
      return this.addRecipient(address, this.bcc);
    }

    public Builder addBcc(Collection<String> address) {
      return this.addRecipients(address, this.bcc);
    }

    private Builder addRecipient(String address, Set<String> recipientsList) {
      Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
      recipientsList.add(address);
      return this;
    }

    private Builder addRecipients(Collection<String> addressList, Set<String> recipientsList) {
      for (String a : addressList) {
        this.addRecipient(a, recipientsList);
      }
      return this;
    }

    public MailObject build() {
      Preconditions.checkArgument(this.body != null && !this.body.isEmpty());
      return new MailObject(this.to, this.cc, this.bcc, this.subject, this.body, this.bodyType);
    }
  }

  public enum BodyType {
    TEXT, HTML
  }

}

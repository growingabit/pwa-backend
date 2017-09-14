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
    return to;
  }

  public Set<String> getCc() {
    return cc;
  }

  public Set<String> getBcc() {
    return bcc;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public static class Builder {

    private String subject;
    private String body;
    private BodyType bodyType;
    private Set<String> to;
    private Set<String> cc;
    private Set<String> bcc;

    public Builder(String to, String subject) {
      this.to = new LinkedHashSet<String>();
      this.cc = new LinkedHashSet<String>();
      this.bcc = new LinkedHashSet<String>();

      Preconditions.checkArgument(EmailValidator.getInstance().isValid(to));
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
      Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
      this.to.add(address);
      return this;
    }

    public Builder addTo(Collection<String> address) {
      for (String a : address) {
        this.addTo(a);
      }
      return this;
    }

    public Builder addCc(String address) {
      Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
      this.cc.add(address);
      return this;
    }

    public Builder addCc(Collection<String> address) {
      for (String a : address) {
        this.addCc(a);
      }
      return this;
    }

    public Builder addBcc(String address) {
      Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
      this.bcc.add(address);
      return this;
    }

    public Builder addBcc(Collection<String> address) {
      for (String a : address) {
        this.addBcc(a);
      }
      return this;
    }

    public MailObject build() {
      Preconditions.checkArgument(body != null && !body.isEmpty());
      return new MailObject(to, cc, bcc, subject, body, bodyType);
    }
  }

  public enum BodyType {
    TEXT, HTML
  }

}

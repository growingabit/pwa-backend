package io.growingabit.app.model;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StudentData {

  @Ignore
  private static final String DATE_FORMAT = "dd/MM/yyyy";

  private String name;
  private String surname;
  private String birthdate;

  public StudentData() {

  }

  public StudentData(final StudentData data) {
    this.setName(data.getName());
    this.setSurname(data.getSurname());
    this.setBirthdate(data.getBirthdate());
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getSurname() {
    return this.surname;
  }

  public void setSurname(final String surname) {
    this.surname = surname;
  }

  public String getBirthdate() {
    return this.birthdate;
  }

  public void setBirthdate(final String birthdate) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(birthdate));
    final DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_FORMAT);
    final DateTime parsedDate = fmt.parseDateTime(birthdate);
    if (!parsedDate.isBeforeNow()) {
      throw new IllegalArgumentException("Cannot set a futureDate as bithdate");
    }
    this.birthdate = birthdate;
  }
}

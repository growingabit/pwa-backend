package io.growingabit.app.model;

public class StudentData {

  private String name;
  private String surname;
  private String birthdate;

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
    this.birthdate = birthdate;
  }
}

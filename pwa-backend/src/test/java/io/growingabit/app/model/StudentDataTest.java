package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

public class StudentDataTest {

  private StudentData studentData;
  private Random random;

  @Before
  public void setup() {
    this.studentData = new StudentData();
    this.random = new Random();
  }

  @Test
  public void validDate() {
    final String birthDate = (this.random.nextInt(28) + 1) + "/" + (this.random.nextInt(12) + 1) + "/" + (this.random.nextInt(100) + 1900);
    this.studentData.setBirthdate(birthDate);

    assertThat(this.studentData.getBirthdate()).isEqualTo(birthDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notAcceptFutureDate() {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    final String birthDate = dateTimeFormatter.print(new DateTime().plusDays(this.random.nextInt(1000)));
    this.studentData.setBirthdate(birthDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notAcceptInvalidDate() {
    final String birthDate = "a non valid date";
    this.studentData.setBirthdate(birthDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notAcceptNullDate() {
    this.studentData.setBirthdate(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notAcceptEmptyDate() {
    this.studentData.setBirthdate("");
  }


}

package io.growingabit.backoffice.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

import io.growingabit.app.model.BaseModel;
import io.growingabit.app.utils.SecureStringGenerator;
import io.growingabit.objectify.ObjectifyUtils;
import io.growingabit.objectify.annotations.Required;

@Entity
@Cache
public class Invitation extends BaseModel {

  @Id
  private Long id;

  @Index
  private String invitationCode;

  @Required
  private String school;
  @Required
  private String schoolClass;
  @Required
  private String schoolYear;
  @Required
  private String specialization;

  @Index
  private String relatedUserId;

  private boolean valid = true;

  public Invitation() {}

  public Invitation(String school, String schoolClass, String schoolYear, String specialization) {
    super();
    this.school = school;
    this.schoolClass = schoolClass;
    this.schoolYear = schoolYear;
    this.specialization = specialization;
  }

  public Long getId() {
    return id;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  public String getSchool() {
    return school;
  }

  public void setSchool(String school) {
    this.school = school;
  }

  public String getSchoolClass() {
    return schoolClass;
  }

  public void setSchoolClass(String schoolClass) {
    this.schoolClass = schoolClass;
  }

  public String getSchoolYear() {
    return schoolYear;
  }

  public void setSchoolYear(String schoolYear) {
    this.schoolYear = schoolYear;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String getRelatedUserId() {
    return relatedUserId;
  }

  public void setRelatedUserId(String relatedUserId) {
    this.relatedUserId = relatedUserId;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  @OnSave
  private void onSave() throws IllegalArgumentException, IllegalAccessException, NullPointerException {
    ObjectifyUtils.checkRequiredFields(this);
    if (this.invitationCode == null) {
      this.invitationCode = new SecureStringGenerator(7).nextString();
    }
  }

}

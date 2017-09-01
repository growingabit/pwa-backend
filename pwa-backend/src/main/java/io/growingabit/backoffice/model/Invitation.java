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

  private boolean confirmed;

  public Invitation() {
  }

  public Invitation(final String school, final String schoolClass, final String schoolYear, final String specialization) {
    super();
    this.school = school;
    this.schoolClass = schoolClass;
    this.schoolYear = schoolYear;
    this.specialization = specialization;
    this.confirmed = false;
  }

  public Long getId() {
    return this.id;
  }

  public String getInvitationCode() {
    return this.invitationCode;
  }

  public void setInvitationCode(final String invitationCode) {
    this.invitationCode = invitationCode;
  }

  public String getSchool() {
    return this.school;
  }

  public void setSchool(final String school) {
    this.school = school;
  }

  public String getSchoolClass() {
    return this.schoolClass;
  }

  public void setSchoolClass(final String schoolClass) {
    this.schoolClass = schoolClass;
  }

  public String getSchoolYear() {
    return this.schoolYear;
  }

  public void setSchoolYear(final String schoolYear) {
    this.schoolYear = schoolYear;
  }

  public String getSpecialization() {
    return this.specialization;
  }

  public void setSpecialization(final String specialization) {
    this.specialization = specialization;
  }

  public String getRelatedUserId() {
    return this.relatedUserId;
  }

  public void setRelatedUserId(final String relatedUserId) {
    this.relatedUserId = relatedUserId;
  }

  public boolean isConfirmed() {
    return this.confirmed;
  }

  public void setConfirmed(final boolean confirmed) {
    this.confirmed = confirmed;
  }

  @OnSave
  private void onSave() throws IllegalArgumentException, IllegalAccessException, NullPointerException {
    ObjectifyUtils.checkRequiredFields(this);
    if (this.invitationCode == null) {
      this.invitationCode = new SecureStringGenerator(7).nextString();
    }
  }

}

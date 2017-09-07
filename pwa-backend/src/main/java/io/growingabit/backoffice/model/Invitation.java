package io.growingabit.backoffice.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import io.growingabit.app.utils.SecureStringGenerator;
import io.growingabit.common.model.BaseModel;
import io.growingabit.objectify.annotations.Required;
import org.apache.commons.lang3.StringUtils;

@Entity
@Cache
public class Invitation extends BaseModel {

  @Id
  private Long id;

  @Index
  @Required
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
    super();
    this.invitationCode = new SecureStringGenerator(7).nextString();
    this.confirmed = false;
  }

  public Invitation(final String school, final String schoolClass, final String schoolYear, final String specialization) {
    this();
    this.school = school;
    this.schoolClass = schoolClass;
    this.schoolYear = schoolYear;
    this.specialization = specialization;
  }

  public Long getId() {
    return this.id;
  }

  public String getInvitationCode() {
    return this.invitationCode;
  }

  public void setInvitationCode(final String invitationCode) {
    if (StringUtils.isNotEmpty(invitationCode)) {
      this.invitationCode = invitationCode;
    }
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

  public void setConfirmed() {
    this.confirmed = true;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Invitation that = (Invitation) o;
    return isConfirmed() == that.isConfirmed() &&
        Objects.equal(getId(), that.getId()) &&
        Objects.equal(getInvitationCode(), that.getInvitationCode()) &&
        Objects.equal(getSchool(), that.getSchool()) &&
        Objects.equal(getSchoolClass(), that.getSchoolClass()) &&
        Objects.equal(getSchoolYear(), that.getSchoolYear()) &&
        Objects.equal(getSpecialization(), that.getSpecialization()) &&
        Objects.equal(getRelatedUserId(), that.getRelatedUserId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), getInvitationCode(), getSchool(), getSchoolClass(), getSchoolYear(), getSpecialization(), getRelatedUserId(), isConfirmed());
  }

}

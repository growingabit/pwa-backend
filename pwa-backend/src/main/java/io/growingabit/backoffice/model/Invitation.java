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

  public Invitation() {}

  public Invitation(String school, String schoolClass, String schoolYear, String specialization) {
    super();
    this.school = school;
    this.schoolClass = schoolClass;
    this.schoolYear = schoolYear;
    this.specialization = specialization;
    this.confirmed = false;
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

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (confirmed ? 1231 : 1237);
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((invitationCode == null) ? 0 : invitationCode.hashCode());
    result = prime * result + ((relatedUserId == null) ? 0 : relatedUserId.hashCode());
    result = prime * result + ((school == null) ? 0 : school.hashCode());
    result = prime * result + ((schoolClass == null) ? 0 : schoolClass.hashCode());
    result = prime * result + ((schoolYear == null) ? 0 : schoolYear.hashCode());
    result = prime * result + ((specialization == null) ? 0 : specialization.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Invitation other = (Invitation) obj;
    if (confirmed != other.confirmed)
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (invitationCode == null) {
      if (other.invitationCode != null)
        return false;
    } else if (!invitationCode.equals(other.invitationCode))
      return false;
    if (relatedUserId == null) {
      if (other.relatedUserId != null)
        return false;
    } else if (!relatedUserId.equals(other.relatedUserId))
      return false;
    if (school == null) {
      if (other.school != null)
        return false;
    } else if (!school.equals(other.school))
      return false;
    if (schoolClass == null) {
      if (other.schoolClass != null)
        return false;
    } else if (!schoolClass.equals(other.schoolClass))
      return false;
    if (schoolYear == null) {
      if (other.schoolYear != null)
        return false;
    } else if (!schoolYear.equals(other.schoolYear))
      return false;
    if (specialization == null) {
      if (other.specialization != null)
        return false;
    } else if (!specialization.equals(other.specialization))
      return false;
    return true;
  }

  @OnSave
  private void onSave() throws IllegalArgumentException, IllegalAccessException, NullPointerException {
    ObjectifyUtils.checkRequiredFields(this);
    if (this.invitationCode == null) {
      this.invitationCode = new SecureStringGenerator(7).nextString();
    }
  }

}

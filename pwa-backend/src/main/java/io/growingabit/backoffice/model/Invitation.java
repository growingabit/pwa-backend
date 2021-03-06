package io.growingabit.backoffice.model;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import io.growingabit.app.model.User;
import io.growingabit.app.utils.SecureStringGenerator;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.common.model.BaseModel;
import io.growingabit.objectify.annotations.Required;

@Entity
@Cache
public class Invitation extends BaseModel {

  @Id
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

  private String relatedUserWebSafeKey;

  private boolean confirmed;

  public Invitation() {
    super();
    do {
      this.invitationCode = new SecureStringGenerator(7).nextString();
    } while (this.isDuplicateInvitation());
    this.confirmed = false;
  }

  public Invitation(final String school, final String schoolClass, final String schoolYear, final String specialization) {
    this();
    this.school = school;
    this.schoolClass = schoolClass;
    this.schoolYear = schoolYear;
    this.specialization = specialization;
  }

  public String getInvitationCode() {
    return this.invitationCode;
  }

  public String getSchool() {
    return this.school;
  }

  public void setSchool(final String school) {
    if (StringUtils.isNotEmpty(school)) {
      this.school = school;
    }
  }

  public String getSchoolClass() {
    return this.schoolClass;
  }

  public void setSchoolClass(final String schoolClass) {
    if (StringUtils.isNotEmpty(schoolClass)) {
      this.schoolClass = schoolClass;
    }
  }

  public String getSchoolYear() {
    return this.schoolYear;
  }

  public void setSchoolYear(final String schoolYear) {
    if (StringUtils.isNotEmpty(schoolYear)) {
      this.schoolYear = schoolYear;
    }
  }

  public String getSpecialization() {
    return this.specialization;
  }

  public void setSpecialization(final String specialization) {
    if (StringUtils.isNotEmpty(specialization)) {
      this.specialization = specialization;
    }
  }

  public String getRelatedUserWebSafeKey() {
    return this.relatedUserWebSafeKey;
  }

  public void setRelatedUserWebSafeKey(final Key<User> relatedUserWebSafeKey) {
    if (relatedUserWebSafeKey != null) {
      this.relatedUserWebSafeKey = relatedUserWebSafeKey.toWebSafeString();
    }
  }

  public boolean isConfirmed() {
    return this.confirmed;
  }

  public void setConfirmed() {
    this.confirmed = true;
  }

  private boolean isDuplicateInvitation() {
    try {
      new InvitationDao().find(Key.create(this));
      return true;
    } catch (final NotFoundException e) {
      return false;
    }
  }

}

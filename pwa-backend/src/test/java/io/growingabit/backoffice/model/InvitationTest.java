package io.growingabit.backoffice.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.BaseDao;
import io.growingabit.testUtils.BaseDatastoreTest;

public class InvitationTest extends BaseDatastoreTest {

    private BaseDao<Invitation> dao;

    @Before
    public void setUp() {
        ObjectifyService.register(Invitation.class);
        this.dao = new BaseDao<>(Invitation.class);
    }

    @Test
    public void requiredFields() {
        Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
        dao.persist(invitation);
        assertThat(invitation.getCreationDate()).isGreaterThan(0L);
    }

    @Test
    public void missingSchoolRequiredField() {
        try {
            Invitation invitation = new Invitation(null, "My class", "This Year", "My Spec");
            dao.persist(invitation);
        } catch (Exception e) {
            assertThat(NullPointerException.class);
        }
    }

    @Test
    public void missingClassRequiredField() {
        try {
            Invitation invitation = new Invitation("My school", null, "This Year", "My Spec");
            dao.persist(invitation);
        } catch (Exception e) {
            assertThat(NullPointerException.class);
        }
    }

    @Test
    public void missingYearRequiredField() {
        try {
            Invitation invitation = new Invitation("My school", "My class", null, "My Spec");
            dao.persist(invitation);
        } catch (Exception e) {
            assertThat(NullPointerException.class);
        }
    }

    @Test
    public void missingSpecRequiredField() {
        try {
            Invitation invitation = new Invitation("My school", "My class", "This Year", null);
            dao.persist(invitation);
        } catch (Exception e) {
            assertThat(NullPointerException.class);
        }
    }

    @Test
    public void checkInvitationCode() {
        Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
        Key<Invitation> key = dao.persist(invitation);
        dao.find(key);
        assertThat(invitation.getInvitationCode().length() == 7);
    }

}
